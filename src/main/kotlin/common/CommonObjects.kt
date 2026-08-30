package findee.common

import java.math.BigDecimal

enum class AccountType(val decode: String) {
    CHECKING("Checking"),
    CREDIT_CARD("Credit Card"),
    SAVINGS("Savings"),
    INVESTMENT("Investment"),
    LOAN("Loan");

    fun isCheckingType(): Boolean {
        return this == CHECKING || this == CREDIT_CARD
    }

    companion object {
        fun fromDecode(decode: String): AccountType? {
            for (at in AccountType.entries) {
                if (decode == at.decode) return at
            }

            return null
        }
    }
}

data class Account(
    val sfinId: String,
    val connId: String,
    val connName: String,
    val balance: BigDecimal,
    val name: String,
    val alias: String?,
    val color: String,
    val type: AccountType
)