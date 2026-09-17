// SPDX-FileCopyrightText: 2026 Zachary Heskett <zheskett@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package findee

import findee.backend.*
import findee.common.*
import findee.db.createTables
import findee.routes.*
import findee.templates.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.v1.jdbc.*
import java.time.*

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
            call.respondHtmlTemplate(BaseTemplate(), code) {
                insideContent {
                    insert(ErrorPage()) {
                        statusCode {
                            +code.value.toString()
                        }
                        message {
                            +"Page not found"
                        }
                    }
                }
            }
        }

        exception<Throwable> { call, cause ->
            call.respondHtmlTemplate(BaseTemplate(), HttpStatusCode.InternalServerError) {
                insideContent {
                    insert(ErrorPage()) {
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

fun Application.scheduleJobs() {
    // No scheduling in debug mode
    val debug = environment.config.property("ktor.debug.debug").getString() == "true"
    if (debug) return

    val hours =
        environment.config
            .property("ktor.schedule.hours")
            .getString()
            .toLong()
    val scheduler =
        Scheduler {
            updateSimpleFin()
        }
    scheduler.scheduleAt(LocalTime.MIDNIGHT, Duration.ofHours(hours))
}
