package findee.templates

import io.ktor.server.html.*
import kotlinx.html.*

class BaseTemplate : Template<HTML> {
    val appName = "Fin-Dee"

    override fun HTML.apply() {
        head {
            title { +appName }
            link("/static/bulma.min.css", "stylesheet")
            link("/static/favicon.png", "icon", "image/png")
            meta("viewport", "width=device-width, initial-scale=1")
        }

        body {
            div {
                h1("title is-1 is-spaced") {
                    +"Fin-Dee"
                }
            }
        }
    }
}