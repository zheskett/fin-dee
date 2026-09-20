// SPDX-FileCopyrightText: 2026 Zachary Heskett <zheskett@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package findee.routes

import findee.db.*
import findee.templates.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.baseRoutes() {
    get("/") {
        call.respondRedirect("/overview", true)
    }

    get("/overview") {
        val accounts = getLatestAccounts()
        call.respondHtmlTemplate(BaseTemplate()) {
            insideContent {
                insert(HomePage(HomePageType.OVERVIEW_PAGE, accounts)) {}
            }
        }
    }

    get("/investments") {
        // TODO: with holdings
        val accounts = getLatestAccounts()
        call.respondHtmlTemplate(BaseTemplate()) {
            insideContent {
                insert(HomePage(HomePageType.INVESTMENTS_PAGE, accounts)) {}
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
