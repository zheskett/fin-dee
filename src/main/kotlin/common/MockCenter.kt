package findee.common

import kotlin.random.Random
import kotlin.uuid.Uuid

object MockCenter {
    fun genAccountSet(): SimpleFinAccountSet {
        val errlist = emptyList<SimpleFinError>()
        TODO()
    }

    private fun genConnectionAndColor(): Pair<SimpleFinConnection, String> {
        return SimpleFinConnection(
            "CONN-${Uuid.random()}",
            connectionNames.random(),
            "IGNORE",
            "IGNORE",
            "IGNORE"
        ) to genRandColor()
    }

    private fun genAccountWithType(connId: String): Pair<SimpleFinAccount, AccountType> {
        val accountEnd = accountSuffixesAndTypes.random()
        return SimpleFinAccount(
            "ACT-${Uuid.random()}",
            "${accountPrefixes.random()} ${accountEnd.first}",
            connId,
            "USD",
            genRandBalance(),
            null,
            0,
            null
        ) to accountEnd.second
    }

    private fun genRandBalance(): String {
        val cents = String.format("%02d", Random.nextInt(100))
        val dollars = Random.nextInt(10_000).toString()
        return "$dollars.$cents"
    }

    private fun genRandAcctNum(): String {
        return "(${Random.nextInt(10000)})"
    }

    private fun genRandColor(): String {
        return Random.nextBytes(3).joinToString("") {
            it.toHexString()
        }
    }
}

private val connectionNames = listOf(
    "James Bank",
    "Thomas Financial",
    "Stem and Leaf",
    "Organized Banking and Investments",
    "Crazy Credit Union",
    "Lazy Capital",
    "Big Bank",
    "Alpaca",
    "Green Federal Banking",
    "Money Masters"
)

private val accountPrefixes = listOf(
    "General",
    "PIZZA",
    "Amazing",
    "Retirement",
    "Traditional",
    "Greatest Cash",
    "Lizard"
)

private val accountSuffixesAndTypes = listOf(
    "Checking" to AccountType.CHECKING,
    "Account" to AccountType.CHECKING,
    "Investments" to AccountType.INVESTMENT,
    "Retirement" to AccountType.SAVINGS,
    "Savings" to AccountType.SAVINGS,
    "Credit Card" to AccountType.CREDIT_CARD,
    "Fund" to AccountType.INVESTMENT,
    "Combo" to AccountType.CHECKING
)