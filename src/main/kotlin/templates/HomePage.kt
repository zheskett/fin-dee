package findee.templates

import io.ktor.htmx.html.hx
import io.ktor.server.html.*
import kotlinx.html.*

class HomePage : Template<FlowContent> {
    override fun FlowContent.apply() {
        div("container is-widescreen") {
            div("box") { +"Hello Fin-Dee" }
        }
    }
}