package findee.templates

import io.ktor.htmx.html.hx
import kotlinx.html.*
import io.ktor.server.html.*
import io.ktor.utils.io.ExperimentalKtorApi

class Modal : Template<FlowContent> {
    val modalTitle = Placeholder<FlowContent>()
    val modalContent = Placeholder<FlowContent>()
    val modalConfirm = Placeholder<BUTTON>()

    @OptIn(ExperimentalKtorApi::class)
    override fun FlowContent.apply() {
        div("modal is-active") {
            id = "modal"
            div("modal-background") {
                attributes["hx-on:click"] = "find('global #modal').remove()"
            }
            div("modal-card") {
                header("modal-card-head") {
                    p("modal-card-title") { insert(modalTitle) }
                    button(classes = "delete is-large") {
                        attributes["hx-on:click"] = "find('global #modal').remove()"
                        attributes.hx {}
                    }
                }
                section("modal-card-body") {
                    div("content") { insert(modalContent) }
                }
                footer("modal-card-foot") {
                    div("buttons") {
                        button(classes = "button is-primary") { insert(modalConfirm) }
                        button(classes = "button") {
                            attributes["hx-on:click"] = "find('global #modal').remove()"
                            +"Close"
                        }
                    }
                }
            }
        }
    }
}