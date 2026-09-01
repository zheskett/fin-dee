package findee.templates

import findee.common.*
import io.ktor.htmx.html.hx
import io.ktor.server.html.*
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.*
import java.math.BigDecimal

class AccountBox(private val account: Account) : Template<FlowContent> {
    private val textColorClass = calcTextColorClass(account.color)
    private val isNeg = account.balance < BigDecimal.ZERO
    private val balanceStr = run {
        val balPart = account.balance.abs().toPlainString()
        val negPart = if (isNeg) "-" else ""
        "$negPart$$balPart"
    }
    private val clickStr =
        """
            find('next .message-body').classList.toggle('is-hidden');
            find('find i').classList.toggle('fa-chevron-down');
            find('find i').classList.toggle('fa-chevron-right');
        """.trimIndent().replace("\n", "")

    @OptIn(ExperimentalKtorApi::class)
    override fun FlowContent.apply() {
        div("block container is-max-desktop") {
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
                    div("buttons") {
                        button(classes = "button") {
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
                        button(classes = "button is-text $textColorClass") {
                            style = "text-decoration: none;"
                            attributes["hx-on:click"] = clickStr
                            span("icon") { i("fa-solid fa-chevron-down") }
                        }
                    }
                }
                div("message-body") {
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