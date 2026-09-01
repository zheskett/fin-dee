package findee.templates

import findee.common.*
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
                        div("field") {
                            label("label") {
                                attributes["for"] = "asf-alias"
                                span("icon mr-1") { i("fa-solid fa-id-card") }
                                span { +"Alias" }
                            }
                            div("control") {
                                input(InputType.text, name = "alias", classes = "input") {
                                    placeholder = account.name
                                    value = account.alias ?: ""
                                }
                            }
                        }
                        div("field") {
                            label("label") {
                                attributes["for"] = "asf-type"
                                span("icon mr-1") { i("fa-solid fa-chart-simple") }
                                span { +"Account Type" }
                            }
                            div("control") {
                                div("select") {
                                    select {
                                        id = "asf-type"
                                        name = "type"
                                        for (at in AccountType.entries) {
                                            option {
                                                selected = account.type == at
                                                +at.decode
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        div("field") {
                            label("label") {
                                attributes["for"] = "asf-color"
                                span("icon mr-1") { i("fa-solid fa-palette") }
                                span { +"Color" }
                            }
                            div("control") {
                                input(InputType.color, name = "color", classes = "input") {
                                    style = "width: 8.5rem;"
                                    value = "#${account.color}"
                                }
                            }
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