// SPDX-FileCopyrightText: 2026 Zachary Heskett <zheskett@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package findee.templates

import findee.common.*
import io.ktor.server.html.*
import kotlinx.html.*

class InvestmentsPage(
    private val accounts: List<Account>?,
) : Template<FlowContent> {
    override fun FlowContent.apply() {
        if (accounts.isNullOrEmpty() || accounts.all { it.holdings.isEmpty() }) {
            section("section") {
                div("container is-max-desktop box is-flex is-justify-content-center") {
                    p("is-size-5") {
                        strong { +"No Applicable Accounts or Investments" }
                    }
                }
            }
            return
        }
    }
}
