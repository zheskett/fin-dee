package findee.db

import findee.common.SimpleFinAccountSet
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.*

fun createTables(db: Database) {
    transaction(db) {
        SchemaUtils.create(UpdateTable, AccountTable, ConnectionTable, ErrorTable, inBatch = true)
    }
}

suspend fun storeUpdate(accountSet: SimpleFinAccountSet, status: HttpStatusCode) {
    suspendTransaction {
        val updateInsert = UpdateTable.insert {
            it[httpCode] = status.value
            it[createdAt] = CurrentTimestampWithTimeZone
        }
        val updateId = updateInsert[UpdateTable.id]
    }
}