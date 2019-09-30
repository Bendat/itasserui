package itasser.app.mytasser.app.login

import arrow.core.Either
import itasser.app.mytasser.app.login.LoginModal.LoginDuration.Actions
import itasser.app.mytasser.app.login.LoginModal.LoginDuration.values
import itasserui.common.logger.Logger
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Alert.AlertType.ERROR
import tornadofx.*

class LoginModal(
    titleText: String = "User Login",
    scope: Scope? = null
) : View(), Logger {
    override val scope: Scope = scope ?: super.scope
    val model: LoginModalModel by inject()

    fun <T : Node> T.id(fxId: String): T {
        id = fxId
        return this
    }

    @Suppress("unused")
    enum class LoginDuration {
        Actions,
        Seconds,
        Minutes,
        Hours
    }

    override val root: Parent = hbox {
        spacer()
        form {
            maxWidth = 520.0
            maxHeight = 250.0
            fieldset(titleText) {
                field("Username") { textfield(model.username).id("username_field") }
                field("Password") { passwordfield(model.password) { }.id("password_field") }
                field("Log in for:") {
                    hbox {
                        val values = values().toList()
                        spinner(-1, 100000, 0, 1, false, model.duration, true) { maxWidth = 80.0 }.id("user_timeout")
                        combobox(model.timeUnit, values) { value = Actions }.id("timeout_unit")
                    }

                }
            }
            hbox {
                spacer()
                button("Cancel") { setOnMouseClicked { currentStage?.close() } }.id("login_cancel")
                spacer()
                button("Login") {
                    setOnMouseClicked {
                        when (val login = model.login()) {
                            is Either.Right -> currentStage?.close()
                            is Either.Left -> alert(
                                ERROR,
                                login.a::class.simpleName?.removeSuffix("Error") ?: "Unknown error",
                                login.a.toString(),
                                owner = currentStage,
                                title = "Couldn't log in"
                            ).also {
                                info { "Created alert $it" }
                            }

                        }
                    }
                }.id("login_login")
            }
        }
        spacer()

    }

}


