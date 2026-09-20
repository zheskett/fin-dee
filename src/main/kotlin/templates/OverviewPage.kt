// SPDX-FileCopyrightText: 2026 Zachary Heskett <zheskett@gmail.com>
//
// SPDX-License-Identifier: GPL-3-or-later

package findee.templates

import findee.common.*
import io.ktor.htmx.html.hx
import io.ktor.server.html.*
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.*
import java.math.BigDecimal

private val sortableLoad =
    """
    new Sortable(this, {
        animation: 150,
        ghostClass: 'sortable-ghost',
        handle: '.handle',
        onEnd: function () { this.option('disabled', true) }
    })
    """.trimIndent().replace("\n", "")
private const val SORTABLE_AFTER = "Sortable.get(this).option('disabled', false)"

class OverviewPage(
    private val accounts: List<Account>?,
) : Template<FlowContent> {
    @OptIn(ExperimentalKtorApi::class)
    override fun FlowContent.apply() {
        insert(OverviewBox(accounts)) {}
        section("py-4") {}
        form(classes = "sortable") {
            attributes["hx-on:load"] = sortableLoad
            attributes["hx-on::after:swap"] = SORTABLE_AFTER
            attributes["hx-status:4xx"] = "swap:outerHTML"
            attributes["hx-status:5xx"] = "swap:outerHTML"
            attributes.hx {
                post = "/api/sort"
                trigger = "end"
                swap = "none"
                target = "body"
            }

            accounts?.forEach { account ->
                val isNeg = account.balance < BigDecimal.ZERO
                val balanceStr = moneyFormat.format(account.balance)

                insert(AccountBox(account)) {
                    boxBody {
                        div("key-value grid is-size-5") {
                            div {
                                span("icon mr-1") { i("fa-solid fa-sack-dollar") }
                                span { +"Balance: " }
                            }
                            span(if (isNeg) "has-text-danger" else "has-text-primary") { strong { +balanceStr } }
                            div {
                                span("icon mr-1") { i("fa-solid fa-chart-simple") }
                                span { +"Type: " }
                            }
                            span { strong { +account.type.decode } }
                        }
                    }
                }
            }
        }
    }
}
