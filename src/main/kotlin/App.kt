package findee

import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import findee.routes.*

fun Application.configureRouting() {
    routing {
        staticResources("/static", "static")
        baseRoutes()
    }
}
