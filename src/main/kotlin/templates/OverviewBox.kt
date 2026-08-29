package findee.templates

import findee.common.Account
import findee.common.AccountType
import findee.common.moneyFormat
import io.ktor.server.html.*
import kotlinx.html.*
import java.math.BigDecimal

class OverviewBox(accounts: List<Account>?) : Template<FlowContent> {
    private val cvPair = getWorth(accounts, false)
    private val nwPair = getWorth(accounts, true)
    private val cvColorClass = if (cvPair.second >= BigDecimal.ZERO) "has-text-primary" else "has-text-danger"
    private val nwColorClass = if (nwPair.second >= BigDecimal.ZERO) "has-text-primary" else "has-text-danger"
    override fun FlowContent.apply() {
        div("block container box is-max-desktop") {
            nav("level") {
                div("level-item has-text-centered") {
                    div {
                        p("heading") {
                            span("icon mr-1") { i("fas fa-credit-card") }
                            span { +"Checking Value" }
                        }
                        p("title $cvColorClass") { +cvPair.first }
                    }
                }
                div("level-item has-text-centered") {
                    div {
                        p("heading") {
                            span("icon mr-1") { i("fas fa-piggy-bank") }
                            span { +"Net Worth" }
                        }
                        p("title $nwColorClass") { +nwPair.first }
                    }
                }
            }
        }
    }
}

private fun getWorth(accounts: List<Account>?, withSavings: Boolean): Pair<String, BigDecimal> {
    if (accounts == null) return "N/A" to BigDecimal.ZERO


    val total = accounts.sumOf {
        if (!withSavings && (it.type == AccountType.SAVINGS || it.type == AccountType.INVESTMENTS))
            BigDecimal.ZERO
        else it.balance
    }

    return moneyFormat.format(total) to total

}