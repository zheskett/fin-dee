package findee.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import findee.templates.*
import io.ktor.htmx.HxResponseHeaders
import io.ktor.http.*
import io.ktor.server.html.*
import io.ktor.server.htmx.*
import io.ktor.server.request.requireHeader
import io.ktor.utils.io.ExperimentalKtorApi

@OptIn(ExperimentalKtorApi::class)
fun Route.hxRoutes() {
    route("api") {
        hx {
            post("/refresh-page") {
                call.response.header(HxResponseHeaders.Refresh, "true")
                call.respond("")
            }
        }
    }
}
