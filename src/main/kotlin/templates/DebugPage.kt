package findee.templates

import io.ktor.server.html.*
import kotlinx.html.*
import io.ktor.htmx.html.hx
import io.ktor.utils.io.ExperimentalKtorApi

class DebugPage : Template<FlowContent> {
    @OptIn(ExperimentalKtorApi::class)
    override fun FlowContent.apply() {
        div("container is-max-widescreen") {
            div("box block content") {
                div("buttons is-centered") {
                    button(classes = "button") {
                        attributes.hx {
                            post = "/api/refresh-page"
                            swap = "none"
                        }
                        span("icon") { i("fa-solid fa-sync-alt") }

                        span { +"Refresh Page" }
                    }
                    button(classes = "button") {
                        attributes.hx {
                            post = "/api/update"
                            swap = "none"
                        }
                        span("icon") { i("fa-solid fa-sync-alt") }

                        span { +"Update SimpleFin" }
                    }
                }
            }
        }
    }
}