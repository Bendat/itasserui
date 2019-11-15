package itasser.app.mytasser.app.process.pane

import itasser.app.mytasser.app.mainview.consoletab.SelectedSequenceEvent
import itasser.app.mytasser.app.process.newDialog.NewSequenceDialog
import itasser.app.mytasser.app.process.pane.widget.ProcessWidget
import itasser.app.mytasser.app.process.pane.widget.dialogWindow
import itasser.app.mytasser.app.styles.MainStylee
import itasserui.common.logger.Logger
import itasserui.lib.process.process.ITasser
import javafx.beans.property.Property
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.ListView
import javafx.scene.control.TabPane
import javafx.scene.control.TextFormatter
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import tornadofx.*

class ProcessPane(scope: Scope? = null) : View("My View"), Logger {
    override val scope: Scope = scope ?: super.scope
    val model: ProcessPaneViewModel by inject()

    override val root = vbox {
        maxWidth = Double.MAX_VALUE
        prefWidth = 200.0
        form {
            maxWidth = Double.MAX_VALUE
            prefWidthProperty().bind(this@vbox.prefWidthProperty())
            fieldset {
                hbox(5) {
                    val cicon = imageview(model.dnaIcon) {
                        addClass(MainStylee.paddedImage2)
                        addClass("console-view-run-pause-icon")
                        fitHeight = 16.0
                        isPreserveRatio = true
                    }
                    button(graphic = cicon) {
                        tooltip("Add new ITASSER sequence")
                        setOnMouseClicked {
                            dialogWindow<NewSequenceDialog>(scope) {}
                        }
                    }
                    field {
                        checkbox("Auto-execute sequences") {
                            tooltip = tooltip("If selected, new ITasser processes will run automatically")
                            this.isSelected = true
                            prefHeightProperty().bind(this@hbox.prefHeightProperty())
                        }
                    }
                    spacer { }

                }
                field("Max running: ") {
                    hbox {
                        textfield(SimpleIntegerProperty(3)) {
                            prefWidth = 60.0
                            textFormatter = TextFormatter<String> { change ->
                                when (change.text.isInt()) {
                                    true -> change
                                    else -> null
                                }
                            }
                        }
                        spacer { }
                    }
                }
            }
        }
        fun icon(icon: Property<Image>) = ImageView(icon.value)
            .apply { addClass(MainStylee.paddedImage2) }
            .apply { fitHeight = 24.0 }
            .apply { padding = insets(5.0) }
            .apply { isPreserveRatio = true }

        val onItemSelected = fun(item: ITasser?) {
            println("New item selected $item")
            item?.let {
                fire(SelectedSequenceEvent(it))
            }
        }
        tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("") {
                graphic = icon(model.runningIcon)
                tooltip("Executing Seqeuences")
                addClass("running-tab")
                listview(model.running) {
                    selectionModel.selectedItemProperty().onChange(onItemSelected)

                    addClass("running-list")
                    widget()
                }
            }

            tab("") {
                graphic = icon(model.queuedIcon)
                addClass("queued-tab")
                tooltip("Queued Seqeuences")
                listview(model.queued) {
                    selectionModel.selectedItemProperty().onChange(onItemSelected)
                    addClass("queued-list")
                    widget()
                }
            }


            tab("") {
                graphic = icon(model.completedIcon)
                tooltip("completed Seqeuences")
                addClass("completed-tab")
                listview(model.completed) {
                    selectionModel.selectedItemProperty().onChange(onItemSelected)

                    addClass("completed-list")
                    widget()
                }
            }

            tab("") {
                graphic = icon(model.pausedIcon)
                tooltip("Paused Sequences")
                addClass("paused-tab")
                listview(model.paused) {
                    selectionModel.selectedItemProperty().onChange(onItemSelected)

                    addClass("paused-list")
                    widget()
                }
            }
            tab("") {
                graphic = icon(model.failedIcon)
                tooltip("Failed Sequences")
                addClass("failed-tab")
                listview(model.failed) {
                    selectionModel.selectedItemProperty().onChange(onItemSelected)

                    addClass("failed-list")
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
