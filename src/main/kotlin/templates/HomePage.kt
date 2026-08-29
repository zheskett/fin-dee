package findee.templates

import findee.common.Account
import io.ktor.htmx.html.hx
import io.ktor.server.html.*
import kotlinx.html.*

class HomePage(private val accounts: List<Account>?) : Template<FlowContent> {
    override fun FlowContent.apply() {
        section("section") {
            insert(OverviewBox(accounts)) {}
        }
    }
}