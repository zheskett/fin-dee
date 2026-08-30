package findee.routes

import findee.backend.updateSimpleFin
import findee.common.getDurationString
import findee.db.getAccountSettings
import findee.db.getLastUpdateTime
import findee.db.getNumUpdatesSinceTime
import io.ktor.server.response.*
import io.ktor.server.routing.*
import findee.templates.*
import io.ktor.htmx.HxResponseHeaders
import io.ktor.htmx.html.hx
import io.ktor.http.*
import io.ktor.server.application.log
import io.ktor.server.html.*
import io.ktor.server.htmx.*
import io.ktor.server.request.requireHeader
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.*
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalKtorApi::class)
fun Route.hxRoutes() {
    route("modal") {
        hx {
            get("/update") {
                val lastUpdate = getLastUpdateTime()
                val updatesIn24Hrs = getNumUpdatesSinceTime(OffsetDateTime.now().minusHours(24))
                val now = OffsetDateTime.now()
                call.respondHtmlFragment {
                    insert(Modal()) {
                        modalTitle { +"Update SimpleFIN?" }
                        modalContent {
                            div("block") {
                                p("has-text-weight-bold") {
                                    +"SimpleFIN expects at most 24 updates a day."
                                    br {}
                                    +"Going beyond that limit may result in a SimpleFIN suspension."
                                }
                                p {
                                    +"Last Update: ${getDurationString(lastUpdate, now)}"
                                    br {}
                                    +"Updates in past 24hrs: $updatesIn24Hrs"
                                }
                            }
                        }
                        modalConfirm {
                            attributes["hx-disable"] = "this"
                            attributes.hx {
                                post = "/api/update"
                                on("before:request", "this.classList.add('is-loading')")
                                on("finally:request", "window.location.reload()")
                                swap = "none"
                            }
                            +"Confirm"
                        }
                    }
                }
            }

            get("/account-settings/{sfinId}") {
                val sfinId = call.pathParameters["sfinId"]
                val account = getAccountSettings(sfinId)
                call.respondHtmlFragment {
                    insert(AccountSettingsModal(account!!)) {}
                }
            }
        }
    }

    route("api") {
        hx {
            post("/refresh-page") {
                call.response.header(HxResponseHeaders.Refresh, "true")
                call.respond(HttpStatusCode.NoContent)
            }

            post("/update") {
                val ok = updateSimpleFin()
                call.response.header(HxResponseHeaders.Refresh, "true")
                call.respond(if (ok) HttpStatusCode.NoContent else HttpStatusCode.InternalServerError)
            }
        }
    }
}
