package itasser.app.mytasser.app.login

import arrow.core.Either
import itasser.app.mytasser.app.login.LoginModal.LoginDuration.Seconds
import itasser.app.mytasser.app.login.LoginModal.LoginDuration.values
import itasserui.common.logger.Logger
import itasserui.common.utils.splitCamelcase
import javafx.scene.Parent
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.scene.control.ButtonType
import tornadofx.*
import itasser.app.mytasser.app.login.LoginModalCss as css
class LoginModal(
    titleText: String = "User Login",
    scope: Scope? = null
) : View(), Logger {
    override val scope: Scope = scope ?: super.scope
    val model: LoginModalModel by inject()
    val controller: LoginModalController by inject()


    @Suppress("unused")
    enum class LoginDuration {
        Seconds,
        Minutes,
        Hours
    }

    override val root: Parent = hbox {
        addClass(css.loginModal)
        spacer()
        form {
            maxWidth = 520.0
            maxHeight = 250.0
            fieldset(titleText) {
                field("Username") {
                    combobox(model.username) {
                        setId(css.loginUsernameField)
                        isEditable = true
                        itemsProperty().bind(model.users)
                    }
                }
                field("Password") {
                    passwordfield(model.password)
                    { setId(css.loginPasswordField) }
                }
                field("Log in for:") {
                    hbox {
                        val values = values().toList()
                        spinner(-1, 100000, 0, 1, false, model.duration, true) {
                            setId(css.loginTimeoutSpinner)
                            maxWidth = 80.0
                        }
                        combobox(model.timeUnit, values) {
                            setId(css.loginTimeoutUnitCombo)
                            value = Seconds
                        }
                    }
                }
            }
            hbox {
                spacer()
                button("Cancel") {
                    setId(css.loginCancelButton)
                    setOnMouseClicked { currentStage?.close() }
                }
                spacer()
                button("Login") {
                    setId(css.loginLoginButton)
                    setOnMouseClicked {
                        when (val login = model.login()) {
                            is Either.Right -> currentStage?.close()
                            is Either.Left -> alert(
                                ERROR,
                                login.a::class.simpleName?.removeSuffix("Error")?.splitCamelcase()
                                    ?: "Unknown error",
                                login.a.toString(),
                                ButtonType.OK,
                                owner = currentStage,
                                title = "Couldn't log in"
                            )
                        }
                    }
                }
            }
        }
        spacer()
    }
}


