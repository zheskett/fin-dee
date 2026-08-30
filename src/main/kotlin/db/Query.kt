package findee.db

import findee.common.Account
import findee.common.SimpleFinAccountSet
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.*
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.OffsetDateTime
import kotlin.random.Random

fun createTables(db: Database) {
    transaction(db) {
        SchemaUtils.create(
            UpdateTable,
            AccountUpdateTable,
            AccountTable,
            ConnectionTable,
            ErrorTable,
            inBatch = true
        )
    }
}

suspend fun storeUpdateError(errorMsg: String?, status: HttpStatusCode) {
    suspendTransaction {
        val updateInsert = UpdateTable.insert {
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

suspend fun storeUpdate(accountSet: SimpleFinAccountSet, status: HttpStatusCode): Boolean {
    return suspendTransaction {
        var isSuccess = true
        val updateInsert = UpdateTable.insert {
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
                            ConnectionTable.selectAll().where { ConnectionTable.sfinId eq connId }
                        )
                    },
                listOf(ConnectionTable.sfinId, ConnectionTable.name)
            )
        }

        for ((sfinId, name, connId) in accountSet.accounts) {
            val randColor = String.format("%06X", Random.nextInt(0xffffff + 1))
            AccountTable.insert(
                Table.Dual
                    .select(
                        stringParam(sfinId), stringParam(connId),
                        stringParam(name), stringParam(randColor)
                    )
                    .where {
                        notExists(
                            AccountTable.selectAll().where { AccountTable.sfinId eq sfinId }
                        )
                    },
                listOf(AccountTable.sfinId, AccountTable.connId, AccountTable.name, AccountTable.color)
            )
        }

        AccountUpdateTable.batchInsert(accountSet.accounts, shouldReturnGeneratedValues = false) {
            this[AccountUpdateTable.actId] = it.id
            this[AccountUpdateTable.updateId] = updateId
            this[AccountUpdateTable.balance] = BigDecimal(it.balance)
        }

        return@suspendTransaction isSuccess
    }

}

suspend fun getLastUpdateTime(): OffsetDateTime? {
    return suspendTransaction {
        UpdateTable.select(UpdateTable.createdAt).orderBy(UpdateTable.createdAt to SortOrder.DESC).limit(1).map {
            it[UpdateTable.createdAt]
        }.getOrNull(0)
    }
}

suspend fun getNumUpdatesSinceTime(dur: OffsetDateTime): Long {
    return suspendTransaction {
        UpdateTable.selectAll().where {
            UpdateTable.createdAt greaterEq dur
        }.count()
    }
}

suspend fun getLatestAccounts(): List<Account>? {
    return suspendTransaction {
        val updateId =
            UpdateTable.select(UpdateTable.id).orderBy(UpdateTable.createdAt to SortOrder.DESC).limit(1).map {
                it[UpdateTable.id]
            }.firstOrNull()

        if (updateId == null) return@suspendTransaction null

        (AccountUpdateTable innerJoin AccountTable).selectAll().where {
            AccountUpdateTable.updateId eq updateId
        }.map {
            Account(
                it[AccountTable.sfinId],
                it[AccountTable.connId],
                it[AccountUpdateTable.balance],
                it[AccountTable.name],
                it[AccountTable.alias],
                it[AccountTable.color],
                it[AccountTable.type]
            )
        }
    }
}

suspend fun getAccountSettings(actId: String?): Account? {
    if (actId == null) return null
    return suspendTransaction {
        AccountTable.selectAll().where {
            AccountTable.sfinId eq actId
        }.map {
            Account(
                it[AccountTable.sfinId],
                it[AccountTable.connId],
                BigDecimal.ZERO,
                it[AccountTable.name],
                it[AccountTable.alias],
                it[AccountTable.color],
                it[AccountTable.type]
            )
        }.firstOrNull()
    }
}