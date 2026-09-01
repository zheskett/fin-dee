package findee.routes

import findee.backend.*
import findee.common.*
import findee.db.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import findee.templates.*
import io.ktor.htmx.HxResponseHeaders
import io.ktor.htmx.html.hx
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.htmx.*
import io.ktor.server.request.*
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
                val sfinId = call.pathParameters["sfinId"]!!
                val account = getAccountSettings(sfinId)
                call.respondHtmlFragment {
                    insert(AccountSettingsModal(account!!)) {}
                }
            }
        }
    }

    route("api") {
        hx {
            post("/update") {
                val ok = updateSimpleFin()
                call.response.header(HxResponseHeaders.Refresh, "true")
                call.respond(if (ok) HttpStatusCode.NoContent else HttpStatusCode.InternalServerError)
            }

            post("/account-settings/{sfinId}") {
                val sfinId = call.pathParameters["sfinId"]!!
                val params = call.receiveParameters()
                val alias =
                    if ((params["alias"] ?: "").isEmpty()) null
                    else params["alias"]
                val type = AccountType.fromDecode(params["type"]!!)!!
                val color = params["color"]!!.substring(1)

                val ok = updateAccountSettings(sfinId, alias, type, color)
                call.response.header(HxResponseHeaders.Refresh, "true")
                call.respond(if (ok) HttpStatusCode.NoContent else HttpStatusCode.InternalServerError)
            }
        }
    }

    route("debug") {
        val isDebugStr = environment.config.property("ktor.debug.debug").getString()
        if (isDebugStr != "true") return@route

        post("reset") {
            resetTables()
            call.response.header(HxResponseHeaders.Refresh, "true")
            call.respond(HttpStatusCode.NoContent)
        }

        post("mock") {
            resetTables()
            MockCenter.mockDB()

            call.response.header(HxResponseHeaders.Refresh, "true")
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
