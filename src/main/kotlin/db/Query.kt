// SPDX-FileCopyrightText: 2026 Zachary Heskett <zheskett@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package findee.db

import findee.common.*
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.*
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.OffsetDateTime

private val tables =
    arrayOf(
        UpdateTable,
        AccountUpdateTable,
        AccountTable,
        ConnectionTable,
        HoldingUpdateTable,
        HoldingTable,
        ErrorTable,
    )

fun createTables(db: Database) {
    transaction(db) {
        SchemaUtils.create(*tables, inBatch = true)
    }
}

suspend fun resetTables() {
    suspendTransaction {
        SchemaUtils.drop(*tables, inBatch = true)
        SchemaUtils.create(*tables, inBatch = true)
    }
}

suspend fun storeUpdateError(
    errorMsg: String?,
    status: HttpStatusCode,
) {
    suspendTransaction {
        val updateInsert =
            UpdateTable.insert {
                it[httpCode] = status.value
                it[createdAt] = CurrentTimestampWithTimeZone
            }
        val updateId = updateInsert[UpdateTable.id]
        val errCode = "${status.value}: ${status.description}"
        val errMsg = errorMsg ?: "none"
        ErrorTable.insert {
            it[ErrorTable.updateId] = updateId
            it[code] = errCode
            it[msg] = errMsg
        }
    }
}

suspend fun storeUpdate(
    accountSet: SimpleFinAccountSet,
    status: HttpStatusCode,
): Boolean {
    return suspendTransaction {
        var isSuccess = true
        val updateInsert =
            UpdateTable.insert {
                it[httpCode] = status.value
            }
        val updateId = updateInsert[UpdateTable.id]

        for ((code, msg) in accountSet.errlist) {
            isSuccess = false
            ErrorTable.insert {
                it[ErrorTable.updateId] = updateId
                it[ErrorTable.code] = code
                it[ErrorTable.msg] = msg
            }
        }

        for ((connId, name) in accountSet.connections) {
            // Insert Ignore does not work in H2
            ConnectionTable.insert(
                Table.Dual
                    .select(stringParam(connId), stringParam(name))
                    .where {
                        notExists(
                            ConnectionTable.selectAll().where { ConnectionTable.sfinId eq connId },
                        )
                    },
                listOf(ConnectionTable.sfinId, ConnectionTable.name),
            )
        }

        for ((sfinId, name, connId, _, _, _, _, _, holdings) in accountSet.accounts) {
            AccountTable.insert(
                Table.Dual
                    .select(stringParam(sfinId), stringParam(connId), stringParam(name))
                    .where {
                        notExists(
                            AccountTable.selectAll().where { AccountTable.sfinId eq sfinId },
                        )
                    },
                listOf(AccountTable.sfinId, AccountTable.connId, AccountTable.name),
            )
        }

        AccountUpdateTable.batchInsert(accountSet.accounts, shouldReturnGeneratedValues = false) {
            this[AccountUpdateTable.actId] = it.id
            this[AccountUpdateTable.updateId] = updateId
            this[AccountUpdateTable.balance] = BigDecimal(it.balance)
        }

        for ((sfinId, _, _, _, _, _, _, _, holdings) in accountSet.accounts) {
            HoldingTable.batchUpsert(holdings, shouldReturnGeneratedValues = false) {
                this[HoldingTable.actId] = sfinId
                this[HoldingTable.holdingId] = it.id
                this[HoldingTable.description] = it.description
                this[HoldingTable.costBasis] = BigDecimal(it.costBasis)
                this[HoldingTable.purchasePrice] = BigDecimal(it.purchasePrice)
                this[HoldingTable.symbol] = it.symbol
            }

            HoldingUpdateTable.batchInsert(holdings, shouldReturnGeneratedValues = false) {
                val shares = BigDecimal(it.shares).setScale(4, RoundingMode.HALF_UP)
                val marketValue = BigDecimal(it.marketValue).setScale(4, RoundingMode.HALF_UP)
                val totalValue = (shares * marketValue).setScale(2, RoundingMode.HALF_UP)
                this[HoldingUpdateTable.actId] = sfinId
                this[HoldingUpdateTable.holdingId] = it.id
                this[HoldingUpdateTable.updateId] = updateId
                this[HoldingUpdateTable.marketValue] = marketValue
                this[HoldingUpdateTable.totalValue] = totalValue
                this[HoldingUpdateTable.shares] = shares
            }
        }

        return@suspendTransaction isSuccess
    }
}

suspend fun getLastUpdateTime(): OffsetDateTime? =
    suspendTransaction {
        UpdateTable
            .select(UpdateTable.createdAt)
            .orderBy(UpdateTable.createdAt to SortOrder.DESC)
            .limit(1)
            .map {
                it[UpdateTable.createdAt]
            }.getOrNull(0)
    }

suspend fun getNumUpdatesSinceTime(dur: OffsetDateTime): Long =
    suspendTransaction {
        UpdateTable
            .selectAll()
            .where {
                UpdateTable.createdAt greaterEq dur
            }.count()
    }

suspend fun getLatestAccounts(): List<Account>? {
    return suspendTransaction {
        val updateId =
            UpdateTable
                .select(UpdateTable.id)
                .orderBy(UpdateTable.createdAt to SortOrder.DESC)
                .limit(1)
                .map {
                    it[UpdateTable.id]
                }.firstOrNull()

        if (updateId == null) return@suspendTransaction null

        ((AccountUpdateTable innerJoin AccountTable) innerJoin ConnectionTable)
            .selectAll()
            .where {
                AccountUpdateTable.updateId eq updateId
            }.orderBy(AccountTable.position)
            .map {
                Account(
                    it[AccountTable.sfinId],
                    it[AccountTable.connId],
                    it[ConnectionTable.name],
                    it[AccountUpdateTable.balance],
                    it[AccountTable.name],
                    it[AccountTable.alias],
                    it[AccountTable.color],
                    it[AccountTable.type],
                )
            }
    }
}

suspend fun getAccountSettings(actId: String): Account? =
    suspendTransaction {
        AccountTable
            .selectAll()
            .where {
                AccountTable.sfinId eq actId
            }.map {
                Account(
                    it[AccountTable.sfinId],
                    it[AccountTable.connId],
                    "PLACEHOLDER",
                    BigDecimal.ZERO,
                    it[AccountTable.name],
                    it[AccountTable.alias],
                    it[AccountTable.color],
                    it[AccountTable.type],
                )
            }.firstOrNull()
    }

suspend fun updateAccountSettings(
    actId: String,
    alias: String?,
    type: AccountType,
    color: String,
): Boolean =
    suspendTransaction {
        AccountTable.update({ AccountTable.sfinId eq actId }) {
            it[AccountTable.alias] = alias
            it[AccountTable.type] = type
            it[AccountTable.color] = color
        }
    } > 0

suspend fun sortAccounts(sortList: List<Pair<String, Int>>): Boolean =
    suspendTransaction {
        sortList.sumOf { (actId, order) ->
            AccountTable.update({ AccountTable.sfinId eq actId }) {
                it[AccountTable.position] = order
            }
        } > 0
    }
