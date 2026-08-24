package findee.db

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.*

fun createTables(db: Database) {
    transaction(db) {
        SchemaUtils.create(UpdateTable, AccountTable, ConnectionTable, ErrorTable, inBatch = true)
    }
}