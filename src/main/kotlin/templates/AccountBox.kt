package findee.templates

import findee.common.Account
import io.ktor.htmx.html.hx
import io.ktor.server.html.*
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.*

class AccountBox(val account: Account) : Template<FlowContent> {
    @OptIn(ExperimentalKtorApi::class)
    override fun FlowContent.apply() {
        div("block container is-max-desktop") {
            article("message") {
                div("message-header") {
                    p("is-size-5") {
                        +(account.alias ?: account.name)
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
                        button(classes = "button is-text") {
                            style = "text-decoration: none;"
                            span("icon") { i("fa-solid fa-chevron-down") }
                        }
                    }
                }
            }
        }
    }
}