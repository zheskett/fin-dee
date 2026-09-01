package findee.common

import findee.db.storeUpdate
import findee.db.updateAccountSettings
import io.ktor.http.HttpStatusCode
import kotlin.random.Random
import kotlin.uuid.Uuid

private const val NUM_CONNECTIONS = 5
private const val MIN_ACCOUNTS = 2
private const val MAX_ACCOUNTS = 8

object MockCenter {
    suspend fun mockDB() {
        val errlist = emptyList<SimpleFinError>()
        val connsAndColors = genConnectionAndColors(NUM_CONNECTIONS)
        val allData = List(Random.nextInt(MIN_ACCOUNTS, MAX_ACCOUNTS + 1)) {
            val cac = connsAndColors.random()
            val awt = genAccountWithType(cac.first.connId)
            AllData(cac.first, awt.first, cac.second, awt.second)
        }.sortedBy { it.conn.name }
        val accountSet = SimpleFinAccountSet(
            errlist,
            connsAndColors.map { it.first },
            allData.map { it.act }
        )

        storeUpdate(accountSet, HttpStatusCode.OK)
        for ((conn, act, color, type) in allData) {
            updateAccountSettings(act.id, null, type, color)
        }
    }

    private data class AllData(
        val conn: SimpleFinConnection,
        val act: SimpleFinAccount,
        val color: String,
        val type: AccountType
    )

    private fun genConnectionAndColors(num: Int): List<Pair<SimpleFinConnection, String>> {
        return connectionNames.shuffled().take(num).map {
            SimpleFinConnection(
                "CONN-${Uuid.random()}",
                it,
                "IGNORE",
                "IGNORE",
                "IGNORE"
            ) to genRandColor()
        }
    }

    private fun genAccountWithType(connId: String): Pair<SimpleFinAccount, AccountType> {
        val accountEnd = accountSuffixesAndTypes.random()
        val prefix = if (accountEnd.second == AccountType.CREDIT_CARD) "-" else ""
        return SimpleFinAccount(
            "ACT-${Uuid.random()}",
            "${accountPrefixes.random()} ${accountEnd.first}",
            connId,
            "USD",
            "$prefix${genRandBalance()}",
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
    "Alpaca Bank",
    "Mµ Banking",
    "Money Masters",
    "Wintergreen One Financial"
)

private val accountPrefixes = listOf(
    "General",
    "PIZZA",
    "Amazing",
    "Retirement",
    "Traditional",
    "Greatest Cash",
    "Lizard",
    "Organized"
)

private val accountSuffixesAndTypes = listOf(
    "Checking" to AccountType.CHECKING,
    "Account" to AccountType.CHECKING,
    "Investments" to AccountType.INVESTMENT,
    "Retirement" to AccountType.SAVINGS,
    "Savings" to AccountType.SAVINGS,
    "Credit Card" to AccountType.CREDIT_CARD,
    "Platinum Card" to AccountType.CREDIT_CARD,
    "Fund" to AccountType.INVESTMENT,
    "Combo" to AccountType.CHECKING
)