package findee.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import findee.templates.*
import io.ktor.server.html.*

fun Route.baseRoutes() {
    get("/") {
        call.respondHtmlTemplate(BaseTemplate()) {}
    }
}