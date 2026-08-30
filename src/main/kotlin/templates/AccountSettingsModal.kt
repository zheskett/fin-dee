package findee.templates

import findee.common.Account
import io.ktor.htmx.html.hx
import io.ktor.server.html.*
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.*

class AccountSettingsModal(val account: Account) : Template<FlowContent> {
    @OptIn(ExperimentalKtorApi::class)
    override fun FlowContent.apply() {
        insert(Modal()) {
            modalTitle { +"Account Settings" }
            modalContent {
                div("block") {
                    form {
                        id = "account-settings-form"
                        label {
                            attributes["for"] = "asf-alias"
                            span("icon mr-1") { i("fa-solid fa-id-card") }
                            span { +"Alias" }
                        }
                        input(InputType.text, name = "alias", classes = "input") {
                            placeholder = account.name
                            value = account.alias ?: ""
                        }
                    }
                }
            }
            modalConfirm {
                attributes["hx-disable"] = "this"
                attributes.hx {
                    post = "/api/account-settings/${account.sfinId}"
                    include = "#account-settings-form"
                    on("before:request", "this.classList.add('is-loading')")
                    on("finally:request", "window.location.reload()")
                    swap = "none"
                }
                +"Confirm"
            }
        }
    }
}