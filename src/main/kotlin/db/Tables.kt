package findee.db

import findee.common.AccountType
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

object AccountUpdateTable : Table("account_updates") {
    val actId = reference("act_id", AccountTable.sfinId)
    val updateId = reference("update_id", UpdateTable.id).index()
    val balance = decimal("balance", 15, 2)

    override val primaryKey = PrimaryKey(actId, updateId)
}

object AccountTable : Table("accounts") {
    val sfinId = varchar("sfin_id", 64)
    val connId = reference("conn_id", ConnectionTable.sfinId).index()
    val name = varchar("name", MAX_VARCHAR)
    val alias = varchar("alias", MAX_VARCHAR).nullable().default(null)
    val color = char("color", 6).check { it regexp "^[0-9a-fA-F]{6}$" }
    val type = enumeration("type", AccountType::class).default(AccountType.CHECKING)

    override val primaryKey = PrimaryKey(sfinId)
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