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
                        span("icon") { i("fas fa-sync-alt") }

                        span { +"Refresh Page" }
                    }
                }
            }
        }
    }
}