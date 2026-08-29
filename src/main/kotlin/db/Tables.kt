package findee.db

import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone

private const val MAX_VARCHAR = 128

object UpdateTable : Table("updates") {
    val id = integer("id").autoIncrement()
    val httpCode = integer("http_code")
    val createdAt = timestampWithTimeZone("created_at").index()
        .defaultExpression(CurrentTimestampWithTimeZone)
    override val primaryKey = PrimaryKey(id)
}

object AccountTable : Table("accounts") {
    val sfinId = varchar("sfin_id", 64).index()
    val updateId = reference("update_id", UpdateTable.id)
    val name = varchar("name", MAX_VARCHAR)
    val balance = decimal("balance", 15, 2)
    val connectionId = reference("connection_id", ConnectionTable.sfinId)

    init {
        index(false, updateId, connectionId)
    }

    override val primaryKey = PrimaryKey(sfinId, updateId)
}

object ConnectionTable : Table("connections") {
    val sfinId = varchar("sfin_id", 64)
    val name = varchar("name", MAX_VARCHAR)

    override val primaryKey = PrimaryKey(sfinId)
}

object ErrorTable : Table("errors") {
    val id = integer("id").autoIncrement()
    val updateId = reference("update_id", UpdateTable.id).index()
    val code = varchar("code", 32)
    val msg = text("msg", eagerLoading = true)
}