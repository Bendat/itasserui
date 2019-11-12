package itasser.app.mytasser.app.mainview.consoletab

import itasserui.app.mytasser.lib.kInject
import itasserui.common.extensions.unless
import itasserui.lib.process.details.ProcessOutput
import itasserui.lib.process.manager.ProcessManager
import itasserui.lib.process.process.ITasser
import itasserui.lib.process.process.STDType
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.util.StringConverter
import tornadofx.*

class ConsoleView(scope: Scope? = null) : View("ITasser console") {
    override val scope = scope ?: super.scope
    val model: ConsoleViewModel by inject()
    val processManager: ProcessManager by kInject()
    override val root = vbox {
        textfield(model.command) { isEditable = false }
        scrollpane {
            isFitToHeight = true
            isFitToWidth = true
            textflow {
                prefHeight = 450.0
                prefWidth = 450.0
                bindChildren(model.stream.value, outMapper)
            }
        }

        hbox {
            maxHeight = 80.0
            button("Play") {
//                onMouseClicked {}
            }
            button("Stop")
        }
    }
}

val outMapper
    get() = fun(item: ProcessOutput): Text {
        return when (item.stdType) {
            STDType.Err -> Text("$[${item.timestamp.toString("MM-dd HH:mm:ss")}]: ${item.item}\n")
            else -> Text("$[${item.timestamp.toString("MM-dd HH:mm:ss")}]: ${item.item}\n")
        }.apply { { fill = Color.RED } unless (item.stdType == STDType.Out) }
    }

val commandConverter
    get() = object : StringConverter<SimpleObjectProperty<ITasser?>>() {
        override fun toString(sequence: SimpleObjectProperty<ITasser?>): String {
            return sequence.value?.command ?: "No sequence run yet"
        }

        override fun fromString(string: String?): SimpleObjectProperty<ITasser?> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }