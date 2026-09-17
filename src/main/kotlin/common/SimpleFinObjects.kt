// SPDX-FileCopyrightText: 2026 Zachary Heskett <zheskett@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package findee.common

import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class SimpleFinError(
    val code: String,
    val msg: String,
    @SerialName("conn_id") val connId: String? = null,
    @SerialName("account_id") val accountId: String? = null,
)

@Serializable
data class SimpleFinConnection(
    @SerialName("conn_id") val connId: String,
    val name: String,
    @SerialName("org_id") val orgId: String,
    @SerialName("org_url") val orgUrl: String? = null,
    @SerialName("sfin_url") val sfinUrl: String,
)

@Serializable
data class SimpleFinAccountSet(
    val errlist: List<SimpleFinError>,
    val connections: List<SimpleFinConnection>,
    val accounts: List<SimpleFinAccount>,
)

@Serializable
data class SimpleFinAccount(
    val id: String,
    val name: String,
    @SerialName("conn_id") val connId: String,
    val currency: String = "USD",
    val balance: String,
    @SerialName("available-balance") val availableBalance: String? = null,
    @SerialName("balance-date") val balanceDate: Long,
    val transactions: List<SimpleFinTransaction> = emptyList(),
    val holdings: List<SimpleFinHolding> = emptyList(),
    // extra ignored
)

@Serializable
data class SimpleFinTransaction(
    val id: String,
    val posted: Long,
    val amount: String,
    val description: String,
    @SerialName("transacted_at") val transactedAt: Long? = null,
    val pending: Boolean = false,
    // extra ignored
)

@Serializable
data class SimpleFinHolding(
    val id: String,
    val created: Long,
    val currency: String = "USD",
    @SerialName("cost_basis") val costBasis: String,
    val description: String,
    @SerialName("market_value") val marketValue: String,
    @SerialName("purchase_price") val purchasePrice: String = "0.00",
    val shares: String,
    val symbol: String,
)
