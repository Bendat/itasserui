package itasser.app.mytasser.app.process.pane

import itasser.app.mytasser.app.events.SelectedSequenceEvent
import itasser.app.mytasser.app.process.newDialog.NewProteinDialog
import itasser.app.mytasser.app.process.pane.widget.ProcessWidget
import itasser.app.mytasser.app.process.pane.widget.dialogWindow
import itasser.app.mytasser.app.styles.MainStylee
import itasserui.common.logger.Logger
import itasserui.lib.process.process.ITasser
import javafx.beans.property.Property
import javafx.scene.control.ListView
import javafx.scene.control.TabPane
import javafx.scene.control.TextFormatter
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.util.StringConverter
import tornadofx.*
import itasser.app.mytasser.app.process.pane.ProcessPaneCss as css

class ProcessPane(scope: Scope? = null) : View("My View"), Logger {
    override val scope: Scope = scope ?: super.scope
    val model: ProcessPaneViewModel by inject()
    val controller: ProcessPaneController by lazy { model.item }
    override val root = vbox {
        maxWidth = Double.MAX_VALUE
        minWidth = 200.0
        form {
            maxWidth = Double.MAX_VALUE
            prefWidthProperty().bind(this@vbox.prefWidthProperty())
            fieldset {
                hbox(5) {
                    val cicon = imageview(model.dnaIcon) {
                        addClass(MainStylee.paddedImage2)
                        fitHeight = 16.0
                        isPreserveRatio = true
                    }
                    button(graphic = cicon) {
                        tooltip("Add new ITASSER sequence")
                        addClass(css.newButton)
                        setOnMouseClicked {
                            dialogWindow<NewProteinDialog>(scope) {}
                        }
                    }
                    field {
                        checkbox("Auto-execute sequences", controller.autoRun) {
                            tooltip = tooltip("If selected, new ITasser processes will run automatically")
                            addClass(css.autoRunToggle)
                            this.isSelected = true
                            prefHeightProperty().bind(this@hbox.prefHeightProperty())
                        }
                    }
                    spacer { }

                }
                field("Max running: ") {
                    hbox {
                        textfield(model.maxExecuting, intConverter) {
                            prefWidth = 60.0
                            addClass(css.maxExecuting)
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
                tooltip("Executing Sequences")
                addClass(css.runningTab)
                listview(model.running) {
                    selectionModel.selectedItemProperty().onChange(onItemSelected)
                    addClass(css.runningList)
                    widget()
                }
            }

            tab("") {
                graphic = icon(model.queuedIcon)
                addClass(css.queuedTab)
                tooltip("Queued Sequences")
                listview(model.queued) {
                    selectionModel.selectedItemProperty().onChange(onItemSelected)
                    addClass(css.queuedList)
                    widget()
                }
            }


            tab("") {
                graphic = icon(model.completedIcon)
                tooltip("Completed Sequences")
                addClass(css.completedTab)
                listview(model.completed) {
                    selectionModel.selectedItemProperty().onChange(onItemSelected)
                    addClass(css.completedList)
                    widget()
                }
            }

            tab("") {
                graphic = icon(model.pausedIcon)
                tooltip("Paused Sequences")
                addClass(css.pausedTab)
                listview(model.paused) {
                    selectionModel.selectedItemProperty().onChange(onItemSelected)

                    addClass(css.pausedList)
                    widget()
                }
            }
            tab("") {
                graphic = icon(model.failedIcon)
                tooltip("Failed Sequences")
                addClass(css.failedTab)
                listview(model.failed) {
                    selectionModel.selectedItemProperty().onChange(onItemSelected)
                    addClass(css.failedList)
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

val intConverter = object : StringConverter<Int>() {
    override fun fromString(string: String?): Int = string?.toInt() ?: -1

    override fun toString(`object`: Int?): String = `object`?.toString() ?: "-1"
}