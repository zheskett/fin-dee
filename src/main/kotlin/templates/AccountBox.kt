// SPDX-FileCopyrightText: 2026 Zachary Heskett <zheskett@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package findee.templates

import findee.common.*
import io.ktor.htmx.html.hx
import io.ktor.server.html.*
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.*
import java.math.BigDecimal

private val clickStr =
    """
    find('next .message-body').classList.toggle('is-hidden');
    const icon = find('find i');
    icon.classList.toggle('fa-chevron-down');
    icon.classList.toggle('fa-chevron-right');
    """.trimIndent().replace("\n", "")

class AccountBox(
    private val account: Account,
) : Template<FlowContent> {
    val boxBody = Placeholder<FlowContent>()
    private val textColorClass = calcTextColorClass(account.color)

    @OptIn(ExperimentalKtorApi::class)
    override fun FlowContent.apply() {
        div("block container is-max-desktop") {
            input(InputType.hidden, name = "order") { value = account.sfinId }
            article("message") {
                div("message-header account-header $textColorClass") {
                    style = "--account-color: #${account.color};"
                    div {
                        p("is-size-5") {
                            +(account.alias ?: account.name)
                        }
                        p("is-size-7 has-text-weight-medium is-italic") {
                            +(account.connName)
                        }
                    }
                    div("handle is-align-self-stretch") {
                        style = "flex: 0.9; margin: -1em 0; min-width: 3em"
                    }
                    div("field is-grouped") {
                        button(type = ButtonType.button, classes = "button") {
                            attributes["hx-disable"] = "this"
                            attributes["hx-status:4xx"] = "swap:outerHTML"
                            attributes["hx-status:5xx"] = "swap:outerHTML"
                            attributes.hx {
                                on("before:request", "this.classList.add('is-loading')")
                                on("finally:request", "this.classList.remove('is-loading')")
                                get = "/modal/account-settings/${account.sfinId}"
                                target = "body"
                                swap = "beforeend"
                            }
                            span("icon") { i("fa-solid fa-gear") }
                        }
                        button(type = ButtonType.button, classes = "button is-text $textColorClass") {
                            style = "text-decoration: none;"
                            attributes["hx-on:click"] = clickStr
                            span("icon") { i("fa-solid fa-chevron-down") }
                        }
                    }
                }
                div("message-body") {
                    insert(boxBody)
                }
            }
        }
    }
}
