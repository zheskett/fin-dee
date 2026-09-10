// SPDX-FileCopyrightText: 2026 Zachary Heskett <zheskett@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package findee.db

import findee.common.AccountType
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone

private const val MAX_VARCHAR = 128
private const val ID_LEN = 64

object UpdateTable : Table("updates") {
    val id = integer("id").autoIncrement()
    val httpCode = integer("http_code")
    val createdAt = timestampWithTimeZone("created_at").index()
        .defaultExpression(CurrentTimestampWithTimeZone)

    override val primaryKey = PrimaryKey(id)
}

object AccountUpdateTable : Table("account_updates") {
    val actId = reference("act_id", AccountTable.sfinId, ReferenceOption.CASCADE)
    val updateId = reference("update_id", UpdateTable.id, ReferenceOption.CASCADE).index()
    val balance = decimal("balance", 15, 2)

    override val primaryKey = PrimaryKey(actId, updateId)
}

object AccountTable : Table("accounts") {
    val sfinId = varchar("sfin_id", ID_LEN)
    val connId = reference("conn_id", ConnectionTable.sfinId, ReferenceOption.CASCADE).index()
    val name = varchar("name", MAX_VARCHAR)
    val position = integer("position").autoIncrement()
    val alias = varchar("alias", MAX_VARCHAR).nullable().default(null)
    val color = char("color", 6).check { it regexp "^[0-9a-fA-F]{6}$" }.default("676767")
    val type = enumeration("type", AccountType::class).default(AccountType.CHECKING)

    override val primaryKey = PrimaryKey(sfinId)
}

object ConnectionTable : Table("connections") {
    val sfinId = varchar("sfin_id", ID_LEN)
    val name = varchar("name", MAX_VARCHAR)

    override val primaryKey = PrimaryKey(sfinId)
}


object HoldingUpdateTable : Table("holding_updates") {
    val actId = varchar("act_id", ID_LEN)
    val holdingId = varchar("holding_id", ID_LEN)
    val updateId = reference("update_id", UpdateTable.id, ReferenceOption.CASCADE).index()
    val marketValue = decimal("market_value", 15, 4)
    val totalValue = decimal("total_value", 15, 2)
    val shares = decimal("shares", 15, 4)

    init {
        foreignKey(actId, holdingId, target = HoldingTable.primaryKey)
    }

    override val primaryKey = PrimaryKey(actId, holdingId, updateId)
}

object HoldingTable : Table("holdings") {
    val actId = reference("act_id", AccountTable.sfinId, ReferenceOption.CASCADE)
    val holdingId = varchar("holding_id", ID_LEN).index()
    val description = text("description", eagerLoading = true)
    val costBasis = decimal("cost_basis", 15, 2)
    val purchasePrice = decimal("purchase_price", 15, 2)
    val symbol = varchar("symbol", 8)

    override val primaryKey = PrimaryKey(actId, holdingId)
}

object ErrorTable : Table("errors") {
    val id = integer("id").autoIncrement()
    val updateId = reference("update_id", UpdateTable.id, ReferenceOption.CASCADE).index()
    val code = varchar("code", 32)
    val msg = text("msg", eagerLoading = true)

    override val primaryKey = PrimaryKey(id)
}