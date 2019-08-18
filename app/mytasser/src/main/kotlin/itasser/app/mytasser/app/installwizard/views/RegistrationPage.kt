package itasser.app.mytasser.app.installwizard.views

import arrow.data.Invalid
import itasser.app.mytasser.app.installwizard.viewmodel.InstallWizardViewModel
import itasserui.common.extensions.validPassword
import itasserui.common.logger.Logger
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.geometry.Orientation.VERTICAL
import javafx.scene.control.TextField
import org.apache.commons.validator.routines.EmailValidator
import tornadofx.*

fun EventTarget.textinput(
    property: ObservableValue<String>,
    op: TextField.() -> Unit = {}
): TextField {
    return textfield(property, op)
        .apply {
            setOnMouseClicked {
                if (System.getProperty("itasserui.testmode") == "true"){
                    clear()
                }
            }
        }
}
fun EventTarget.passwordinput(
    property: ObservableValue<String>,
    op: TextField.() -> Unit = {}
): TextField {
    return passwordfield(property, op)
        .apply {
            setOnMouseClicked {
                if (System.getProperty("itasserui.testmode") == "true"){
                    clear()
                }
            }
        }
}

class RegistrationPage : View("Create Admin Account"), Logger {
    val model by inject<InstallWizardViewModel>()
    override val complete get() = model.valid(model.name, model.email, model.password, model.passwordRepeat)

    override val root = form {
        minWidth = 250.0
        fieldset("Create an Administrator Account", labelPosition = VERTICAL) {
            field("Name") {
                textinput(model.name) {
                    addClass("name")
                    validator {
                        if (text.isNullOrBlank()) error()
                        else null
                    }
                }.required(message = "Name cannot be empty.")
            }
            field("Email") {
                textinput(model.email) {
                    addClass("email")
                    validator {
                        val vld = EmailValidator.getInstance()
                        if (vld.isValid(text) or text.isNullOrBlank()) null
                        else error()
                    }
                }.required(message = "Email address is optional but must be valid")
            }
            field("Password") {
                passwordinput(model.password) {
                    addClass("password")
                    validator {
                        val vldtr = text?.validPassword()
                        when (vldtr) {
                            is Invalid -> error(vldtr.e.message)
                            null -> error()
                            else -> null
                        }
                    }
                }.required(message = "Password cannot be empty")

            }
            field("Repeat") {
                passwordinput(model.passwordRepeat) {
                    addClass("password-repeat")
                    setOnMouseClicked { text = null }
                    validator(ValidationTrigger.OnBlur) {
                        if (text == model.password.value) null
                        else error()
                    }
                }.required()
            }
        }
    }
}