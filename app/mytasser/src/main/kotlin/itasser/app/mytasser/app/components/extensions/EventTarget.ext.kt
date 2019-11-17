package itasser.app.mytasser.app.components.extensions

import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import tornadofx.CssRule
import tornadofx.passwordfield
import tornadofx.textfield


fun <T : TextField> EventTarget.stringInput(
    fieldType: () -> T
): T {
    return fieldType().apply {
        setOnMouseClicked {
            if (System.getProperty("itasserui.testmode") == "true") {
                clear()
            }
        }
    }
}

fun EventTarget.textinput(
    property: ObservableValue<String>,
    op: TextField.() -> Unit = {}
): TextField {
    return stringInput { textfield(property, op) }
}

fun EventTarget.passwordinput(
    property: ObservableValue<String>,
    op: TextField.() -> Unit = {}
): PasswordField {
    return stringInput { passwordfield(property, op) }
}

fun Node.fxId(id: String) {
    this.id = id
}

fun Node.fxId(id: CssRule) {
    this.id = id.render()
}