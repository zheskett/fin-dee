package findee.common

import java.math.BigDecimal

enum class AccountType(val decode: String) {
    CHECKING("Checking"),
    CREDIT_CARD("Credit Card"),
    SAVINGS("Savings"),
    INVESTMENTS("Investments"),
}

data class Account(
    val sfinId: String,
    val connId: String,
    val balance: BigDecimal,
    val name: String,
    val alias: String?,
    val color: String,
    val type: AccountType
)