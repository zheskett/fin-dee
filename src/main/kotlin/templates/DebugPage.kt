package findee.templates

import io.ktor.server.html.*
import kotlinx.html.*
import io.ktor.htmx.html.hx
import io.ktor.utils.io.ExperimentalKtorApi

class DebugPage : Template<FlowContent> {
    @OptIn(ExperimentalKtorApi::class)
    override fun FlowContent.apply() {
        div("container box block is-max-desktop") {
            div("buttons is-centered") {
                button(classes = "button") {
                    attributes.hx {
                        post = "/api/update"
                        swap = "none"
                    }
                    span("icon") { i("fa-solid fa-rotate") }
                    span { +"Update SimpleFin" }
                }

                button(classes = "button is-danger is-outlined") {
                    attributes.hx {
                        post = "/debug/reset"
                    }
                    span("icon") { i("fa-solid fa-bomb") }
                    span { +"Reset Database" }
                }

                button(classes = "button is-danger is-outlined") {
                    attributes.hx {
                        post = "/debug/mock"
                    }
                    span("icon") { i("fa-solid fa-ghost") }
                    span { +"Mock Database" }
                }
            }
        }
    }
}