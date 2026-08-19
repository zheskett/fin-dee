package findee.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import findee.templates.*
import io.ktor.server.html.*
import kotlinx.html.div

fun Route.baseRoutes() {
    get("/") {
        call.respondHtmlTemplate(BaseTemplate(HomePage())) {
            insideContent {}
        }
    }

    get("/debug") {
        call.respondHtmlTemplate(BaseTemplate(DebugPage())) {
            insideContent {}
        }
    }
}