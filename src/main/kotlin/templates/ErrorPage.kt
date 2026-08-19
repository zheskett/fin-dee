package findee.templates

import io.ktor.htmx.html.hx
import io.ktor.server.html.*
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.*

class ErrorPage : Template<FlowContent> {
    val statusCode = Placeholder<FlowContent>()
    val message = Placeholder<FlowContent>()

    @OptIn(ExperimentalKtorApi::class)
    override fun FlowContent.apply() {
        div("container is-max-tablet") {
            div("notification is-danger") {
                div("content block") {
                    h1 {
                        +"Error: "
                        insert(statusCode)
                    }
                    p {
                        insert(message)
                    }
                }
                a("/", classes = "mt-6 button") {
                    attributes.hx {
                        boost = true
                    }
                    +"Return Home"
                }
            }
        }
    }
}