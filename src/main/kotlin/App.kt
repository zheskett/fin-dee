package findee

import findee.backend.client
import findee.db.createTables
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import findee.routes.*
import findee.templates.BaseTemplate
import findee.templates.ErrorPage
import io.ktor.http.HttpStatusCode
import io.ktor.server.html.respondHtmlTemplate
import io.ktor.server.plugins.statuspages.*
import org.jetbrains.exposed.v1.jdbc.*

fun Application.configureRouting() {
    routing {
        staticResources("/static", "static")
        baseRoutes()
        hxRoutes()
    }
}

fun Application.configureStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, code ->
            call.respondHtmlTemplate(BaseTemplate(ErrorPage()), code) {
                insideContent {
                    statusCode {
                        +code.value.toString()
                    }
                    message {
                        +"Page not found"
                    }
                }
            }
        }

        exception<Throwable> { call, cause ->
            call.respondHtmlTemplate(BaseTemplate(ErrorPage()), HttpStatusCode.InternalServerError) {
                insideContent {
                    statusCode {
                        +HttpStatusCode.InternalServerError.value.toString()
                    }
                    message {
                        +"$cause"
                    }
                }
            }
        }
    }
}

fun Application.monitors() {
    monitor.subscribe(ApplicationStopped) {
        client.close()
    }
}

fun Application.dbConnect() {
    val dbDir = environment.config.property("ktor.db.dir").getString()
    val db = Database.connect("jdbc:h2:$dbDir/h2", "org.h2.Driver")
    createTables(db)
}