// SPDX-FileCopyrightText: 2026 Zachary Heskett <zheskett@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package findee.routes

import findee.db.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import findee.templates.*
import io.ktor.server.html.*

fun Route.baseRoutes() {
    get("/") {
        val accounts = getLatestAccounts()
        call.respondHtmlTemplate(BaseTemplate()) {
            insideContent {
                insert(HomePage(accounts)) {}
            }
        }
    }

    get("/debug") {
        if (environment.config.property("ktor.debug.debug").getString() != "true") return@get
        call.respondHtmlTemplate(BaseTemplate()) {
            insideContent {
                insert(DebugPage()) {}
            }
        }
    }
}