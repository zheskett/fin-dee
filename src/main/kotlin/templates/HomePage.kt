// SPDX-FileCopyrightText: 2026 Zachary Heskett <zheskett@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package findee.templates

import findee.common.Account
import io.ktor.htmx.html.hx
import io.ktor.server.html.*
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.*

enum class HomePageType(
    val decode: String,
) {
    OVERVIEW_PAGE("Overview"),
    INVESTMENTS_PAGE("Investments"),
}

class HomePage(
    private val page: HomePageType,
    private val accounts: List<Account>?,
) : Template<FlowContent> {
    @OptIn(ExperimentalKtorApi::class)
    override fun FlowContent.apply() {
        div("container is-max-desktop") {
            div("tabs is-centered is-max-desktop") {
                ul {
                    li(if (page == HomePageType.OVERVIEW_PAGE) "is-active" else "") {
                        a("/overview") {
                            attributes.hx {
                                boost = true
                            }
                            +HomePageType.OVERVIEW_PAGE.decode
                        }
                    }
                    li(if (page == HomePageType.INVESTMENTS_PAGE) "is-active" else "") {
                        a("/investments") {
                            attributes.hx {
                                boost = true
                            }
                            +HomePageType.INVESTMENTS_PAGE.decode
                        }
                    }
                }
            }
        }
        when (page) {
            HomePageType.OVERVIEW_PAGE -> {
                insert(OverviewPage(accounts)) {}
            }

            HomePageType.INVESTMENTS_PAGE -> {
                insert(InvestmentsPage(accounts)) {}
            }
        }
    }
}
