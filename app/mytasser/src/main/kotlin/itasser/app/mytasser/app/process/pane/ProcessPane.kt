package itasser.app.mytasser.app.process.pane

import itasser.app.mytasser.app.process.newDialog.NewProcessDialog
import itasser.app.mytasser.app.process.pane.widget.ProcessWidget
import itasser.app.mytasser.app.process.pane.widget.dialogWindow
import itasserui.app.mytasser.lib.DI
import itasserui.lib.process.process.ITasser
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.ListView
import javafx.scene.control.ScrollPane
import tornadofx.*

class ProcessPane(di: DI, scope: Scope? = null) : View("My View") {
    override val scope: Scope = scope ?: super.scope
    val model: ProcessPaneViewModel by inject()

    init {
        setInScope(di, this.scope)
    }

    override val root = vbox {
        prefHeight = 500.0
        prefWidth = 250.0
        form {
            prefWidthProperty().bind(this@vbox.prefWidthProperty())
            fieldset {
                hbox(5) {
                    field {
                        checkbox("...") {
                            tooltip = tooltip("If selected, new ITasser processes will run automatically")
                            this.isSelected = true
                            prefHeightProperty().bind(this@hbox.prefHeightProperty())
                        }
                    }
                    spacer { }
                    button("+") {
                        setOnMouseClicked {
                            dialogWindow<NewProcessDialog>(scope) {}
                        }
                    }
                }
                field("Max running: ") {
                    textfield(SimpleIntegerProperty(3)) {}
                }
            }
        }
        scrollpane {
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            squeezebox {

                fold("Failed") {
                    addClass("failed-fold")
                    listview(model.failed) {
                        addClass("failed-list")
                        widget()
                    }
                }
                fold("Completed") {
                    addClass("completed-fold")
                    listview(model.completed) {
                        addClass("completed-list")
                        widget()
                    }
                }
                fold("Paused") {
                    addClass("paused-fold")
                    listview(model.paused) {
                        addClass("paused-list")
                        widget()
                    }
                }

                fold("Runnning") {
                    addClass("running-fold")
                    listview(model.running) {
                        addClass("running-list")
                        widget()
                    }
                }

                fold("Queued") {
                    addClass("queued-fold")
                    listview(model.queued) {
                        addClass("queued-list")
                        widget()
                    }
                }
            }
        }
    }

    private fun ListView<ITasser>.widget() {
        return cellFormat { process ->
            val user = model.profileManager.find(process.process.createdBy)?.user
            user?.let { graphic = ProcessWidget(user, process).root }
        }
    }
}
