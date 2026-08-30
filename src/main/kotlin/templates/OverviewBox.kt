package findee.templates

import findee.common.Account
import findee.common.AccountType
import findee.common.moneyFormat
import io.ktor.server.html.*
import kotlinx.html.*
import java.math.BigDecimal

class OverviewBox(accounts: List<Account>?) : Template<FlowContent> {
    private val cvPair = getWorth(accounts, true)
    private val nwPair = getWorth(accounts, false)
    private val cvColorClass = if (cvPair.second >= BigDecimal.ZERO) "has-text-primary" else "has-text-danger"
    private val nwColorClass = if (nwPair.second >= BigDecimal.ZERO) "has-text-primary" else "has-text-danger"
    override fun FlowContent.apply() {
        section("section") {
            div("block container box is-max-desktop") {
                id = "overview_box"
                nav("level") {
                    div("level-item has-text-centered") {
                        div {
                            p("heading") {
                                span("icon mr-1") { i("fa-solid fa-credit-card") }
                                span { +"Checking Value" }
                            }
                            p("title $cvColorClass") { +cvPair.first }
                        }
                    }
                    div("level-item has-text-centered") {
                        div {
                            p("heading") {
                                span("icon mr-1") { i("fa-solid fa-piggy-bank") }
                                span { +"Net Worth" }
                            }
                            p("title $nwColorClass") { +nwPair.first }
                        }
                    }
                }
            }
        }
    }
}

private fun getWorth(accounts: List<Account>?, checkingOnly: Boolean): Pair<String, BigDecimal> {
    if (accounts == null) return "N/A" to BigDecimal.ZERO


    val total = accounts.sumOf {
        if (checkingOnly && !it.type.isCheckingType())
            BigDecimal.ZERO
        else it.balance
    }

    return moneyFormat.format(total) to total

}