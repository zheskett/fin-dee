package findee.db

import findee.common.SimpleFinAccountSet
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.*
import java.math.BigDecimal
import java.time.Duration
import java.time.OffsetDateTime

fun createTables(db: Database) {
    transaction(db) {
        SchemaUtils.create(UpdateTable, AccountTable, ConnectionTable, ErrorTable, inBatch = true)
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
            it[createdAt] = CurrentTimestampWithTimeZone
        }
        val updateId = updateInsert[UpdateTable.id]

        accountSet.errlist.forEach { item ->
            isSuccess = false
            ErrorTable.insert {
                it[ErrorTable.updateId] = updateId
                it[code] = item.code
                it[msg] = item.msg
            }
        }

        accountSet.connections.forEach { conn ->
            // Insert Ignore does not work in H2
            ConnectionTable.insert(
                Table.Dual
                    .select(stringParam(conn.connId), stringParam(conn.name))
                    .where {
                        notExists(
                            ConnectionTable.selectAll().where { ConnectionTable.sfinId eq conn.connId }
                        )
                    },
                listOf(ConnectionTable.sfinId, ConnectionTable.name)
            )
        }

        AccountTable.batchInsert(accountSet.accounts, shouldReturnGeneratedValues = false) {
            this[AccountTable.sfinId] = it.id
            this[AccountTable.updateId] = updateId
            this[AccountTable.name] = it.name
            this[AccountTable.balance] = BigDecimal(it.balance)
            this[AccountTable.connectionId] = it.connId
        }

        return@suspendTransaction isSuccess
    }

}

suspend fun getLastUpdateTime(): OffsetDateTime? {
    return suspendTransaction {
        UpdateTable.select(UpdateTable.createdAt).limit(1).orderBy(UpdateTable.createdAt to SortOrder.DESC).map {
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