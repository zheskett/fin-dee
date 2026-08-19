package findee.templates

import io.ktor.htmx.html.hx
import io.ktor.server.html.*
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.*

class BaseTemplate<T : Template<FlowContent>>(private val inner: T) : Template<HTML> {
    val insideContent = TemplatePlaceholder<T>()
    private val appName = "Fin-Dee"

    @OptIn(ExperimentalKtorApi::class)
    override fun HTML.apply() {
        head {
            title { +appName }
            link("/static/bulma.min.css", "stylesheet")
            link("/static/fontawesome/css/all.min.css", "stylesheet")
            link("/static/styles.css", "stylesheet")
            link("/static/favicon.png", "icon", "image/png")
            meta("viewport", "width=device-width, initial-scale=1")
            script("text/javascript", "/static/htmx.min.js") {}
        }

        body {
            header("block") {
                nav("navbar is-primary is-spaced") {
                    div("navbar-brand") {
                        a("/", classes = "navbar-item") {
                            attributes.hx {
                                boost = true
                            }
                            img(appName, "/static/logo.svg") {
                                width = "48"
                            }
                            h1("is-size-2 has-text-primary-invert is-family-maloney") {
                                +appName
                            }
                        }
                    }
                    div("navbar-end") {
                        div("navbar-item") {
                            a("/debug", classes = "button is-danger") {
                                attributes.hx {
                                    boost = true
                                }
                                +"Debug"
                            }
                        }
                    }
                }
            }
            insert(inner, insideContent)
        }
    }
}