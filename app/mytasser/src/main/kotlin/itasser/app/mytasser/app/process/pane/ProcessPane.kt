package itasser.app.mytasser.app.process.pane

import itasser.app.mytasser.app.process.newDialog.NewProcessDialog
import itasser.app.mytasser.app.process.pane.widget.ProcessWidget
import itasser.app.mytasser.app.process.pane.widget.dialogWindow
import itasserui.common.logger.Logger
import itasserui.lib.process.process.ITasser
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.ListView
import tornadofx.*

class ProcessPane(scope: Scope? = null) : View("My View"), Logger {
    override val scope: Scope = scope ?: super.scope
    val model: ProcessPaneViewModel by inject()

    override val root = vbox {
        info { "Processes are ${model.item.processManager.processes.all.toList()}" }
        fitToParentWidth()
        form {
            prefWidthProperty().bind(this@vbox.prefWidthProperty())
            fieldset {
                hbox(5) {
                    field {
                        checkbox("Auto-execute sequences") {
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

        tabpane {
            tab("Failed") {
                addClass("failed-tab")
                listview(model.failed) {
                    addClass("failed-list")
                    widget()
                }
            }
            tab("Completed") {

                addClass("completed-tab")
                listview(model.completed) {
                    addClass("completed-list")
                    widget()
                }
            }
            tab("Paused") {
                addClass("paused-tab")
                listview(model.paused) {
                    addClass("paused-list")
                    widget()
                }
            }

            tab("Running") {
                addClass("running-tab")
                anchorpane {
                    fitToParentHeight()
                    listview(model.running) {
                        addClass("running-list")
                        widget()
                    }
                }
            }

            tab("Queued") {
                addClass("queued-tab")
                listview(model.queued) {
                    this.selectionModelProperty().onChange { item ->
                        info { "Changee to ${item}" }
                    }
                    addClass("queued-list")
                    widget()
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
