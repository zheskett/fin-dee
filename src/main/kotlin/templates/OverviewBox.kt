// SPDX-FileCopyrightText: 2026 Zachary Heskett <zheskett@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package findee.templates

import findee.common.Account
import findee.common.AccountType
import findee.common.moneyFormat
import io.ktor.htmx.html.hx
import io.ktor.server.html.*
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.*
import java.math.BigDecimal

class OverviewBox(
    accounts: List<Account>?,
) : Template<FlowContent> {
    private val cvPair = getWorth(accounts, true)
    private val nwPair = getWorth(accounts, false)
    private val cvColorClass = if (cvPair.second >= BigDecimal.ZERO) "has-text-primary" else "has-text-danger"
    private val nwColorClass = if (nwPair.second >= BigDecimal.ZERO) "has-text-primary" else "has-text-danger"

    @OptIn(ExperimentalKtorApi::class)
    override fun FlowContent.apply() {
        section("section") {
            div("container box is-max-desktop is-relative") {
                id = "overview_box"
                nav("level mb-0") {
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
                button(classes = "button") {
                    style = "position: absolute; top: 0.75rem; right: 0.75rem;"
                    attributes["hx-disable"] = "this"
                    attributes["hx-status:4xx"] = "swap:outerHTML"
                    attributes["hx-status:5xx"] = "swap:outerHTML"
                    attributes.hx {
                        on("before:request", "this.classList.add('is-loading')")
                        on("finally:request", "this.classList.remove('is-loading')")
                        get = "/modal/update"
                        target = "body"
                        swap = "beforeend"
                    }
                    span("icon") { i("fa-solid fa-rotate") }
                }
            }
        }
    }
}

private fun getWorth(
    accounts: List<Account>?,
    checkingOnly: Boolean,
): Pair<String, BigDecimal> {
    if (accounts == null) return "N/A" to BigDecimal.ZERO

    val total =
        accounts.sumOf {
            if (checkingOnly && !it.type.isCheckingType()) {
                BigDecimal.ZERO
            } else {
                it.balance
            }
        }

    return moneyFormat.format(total) to total
}
