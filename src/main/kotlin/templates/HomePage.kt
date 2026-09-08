package findee.templates

import findee.common.Account
import io.ktor.htmx.html.hx
import io.ktor.server.html.*
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.*


private val sortableLoad = """
    new Sortable(this, {
        animation: 150,
        ghostClass: 'sortable-ghost',
        filter: '.no-drag',
        preventOnFilter: false,
        handle: '.handle',
        onEnd: function () { this.option('disabled', true) }
    })
""".trimIndent().replace("\n", "")
private const val sortableAfter = "Sortable.get(this).option('disabled', false)"


class HomePage(private val accounts: List<Account>?) : Template<FlowContent> {
    @OptIn(ExperimentalKtorApi::class)
    override fun FlowContent.apply() {
        insert(OverviewBox(accounts)) {}
        section("py-4") {}
        form(classes = "sortable") {
            attributes["hx-on:load"] = sortableLoad
            attributes["hx-on::after:swap"] = sortableAfter
            attributes["hx-status:4xx"] = "swap:outerHTML"
            attributes["hx-status:5xx"] = "swap:outerHTML"
            attributes.hx {
                post = "/api/sort"
                trigger = "end"
                swap = "none"
                target = "body"
            }

            accounts?.forEach {
                insert(AccountBox(it)) {}
            }
        }
    }
}