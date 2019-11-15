package itasser.app.mytasser.app.mainview.consoletab

import itasser.app.mytasser.app.process.pane.widget.WidgetCss
import itasser.app.mytasser.app.process.pane.widget.loginModal
import itasser.app.mytasser.app.styles.MainStylee
import itasser.app.mytasser.lib.kInject
import itasserui.common.extensions.unless
import itasserui.lib.process.details.ProcessOutput
import itasserui.lib.process.manager.ProcessManager
import itasserui.lib.process.process.ITasser
import itasserui.lib.process.process.STDType
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.util.StringConverter
import org.controlsfx.control.Notifications
import tornadofx.*

class ConsoleView(scope: Scope? = null) : View("ITasser console") {
    override val scope = scope ?: super.scope
    val model: ConsoleViewModel by inject()
    val processManager: ProcessManager by kInject()
    override val root = vbox {

        textfield(model.command) {
            addClass("sequence-console-command")
            isEditable = false
        }
        scrollpane {
            isFitToHeight = true
            isFitToWidth = true
            textflow {
                addClass("console-view-textflow")
                prefHeight = 450.0
                prefWidth = 450.0
                bindChildren(model.stream.value, outMapper)
            }
        }

        hbox {
            spacer()
            maxHeight = 80.0
            val ricon = imageview(model.runPauseIcon) {
                addClass(MainStylee.paddedImage2)
                addClass("console-widget-run-pause-icon")
                fitHeight = 32.0
                isPreserveRatio = true
            }
            button(graphic = ricon) {
                addClass(MainStylee.transparentButton)
                addClass(WidgetCss.controlButton)
                model.executionStateProperty
                    .addListener { _, _, new -> model.setRunPlayIcon(new) }
                setOnMouseClicked {
                    model.perform({ loginModal("") }) {
                        processManager.run(it)
                    }
                }
            }
            val dicon = imageview(model.stopIcon) {
                addClass(MainStylee.paddedImage2)
                addClass("process-widget-run-pause-icon")
                fitHeight = 32.0
                isPreserveRatio = true
            }
            button(graphic = dicon) {
                addClass(MainStylee.transparentButton)
                addClass(WidgetCss.controlButton)

                processManager
            }
            val cicon = imageview(model.copyIcon) {
                addClass(MainStylee.paddedImage2)
                addClass("process-widget-run-pause-icon")
                fitHeight = 32.0
                isPreserveRatio = true
            }
            button(graphic = cicon) {
                setOnMouseClicked {
                    clipboard.putString(model.item.stdStream.joinToString { outMapper(it).text })
                    Notifications.create()
                        .text("Copied output to clipboard")
                        .showInformation()
                }
            }
            spacer()

        }
    }
}

val outMapper
    get() = fun(item: ProcessOutput): Text {
        return when (item.stdType) {
            STDType.Err -> Text("[${item.timestamp.toString("MM-dd HH:mm:ss")}]: ${item.item}\n")
            else -> Text("[${item.timestamp.toString("MM-dd HH:mm:ss")}]: ${item.item}\n")
        }.apply { { fill = Color.RED } unless (item.stdType == STDType.Out) }
            .apply { if (item.stdType is STDType.Err) addClass("err-text") }
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