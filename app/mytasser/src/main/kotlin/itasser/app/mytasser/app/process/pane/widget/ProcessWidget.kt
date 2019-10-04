package itasser.app.mytasser.app.process.pane.widget

import itasser.app.mytasser.app.styles.MainStylee
import itasser.app.mytasser.app.styles.MainStylee.Companion.paddedImage2
import itasser.app.mytasser.app.styles.MainStylee.Companion.transparentButton
import itasser.app.mytasser.lib.DI
import itasserui.app.user.User
import itasserui.common.extensions.format
import itasserui.common.logger.Logger
import itasserui.lib.process.manager.ProcessManager
import itasserui.lib.process.process.ITasser
import javafx.animation.AnimationTimer
import javafx.util.StringConverter
import org.joda.time.DateTime
import org.kodein.di.generic.instance
import tornadofx.*
import java.time.Duration.ofMillis

class ProcessWidget(
    user: User,
    itasser: ITasser,
    scope: Scope? = null
) : Fragment("Process Widget Fragment"), Logger {
    override val scope: Scope = scope ?: super.scope

    val kdi: DI by lazy { val d = find<DI>(); d }
    val model: ProcessWidgetViewModel = ProcessWidgetViewModel(user, itasser)

    private val processManager: ProcessManager by lazy { val i by kdi.instance<ProcessManager>(); i }
    private val iconHeight: Double = 16.0

    init {
        setInScope(model, this.scope)
    }

    override val root = anchorpane {
        minWidth = 250.0
        addStylesheet(MainStylee::class)
        vbox {
            hbox {
                label("  ")
                borderpane {
                    left {
                        vbox {
                            hbox {
                                imageview(resources.image("/icons/users.png")) {
                                    addClass(paddedImage2)
                                    fitHeight = iconHeight
                                    isPreserveRatio = true
                                }
                                label(" ")
                                label(model.username) {
                                    addClass("process-widget-username-label")
                                }
                            }
                            hbox {
                                imageview(resources.image("/icons/stopwatch.png")) {
                                    this.isSmooth = true
                                    addClass(paddedImage2)
                                    fitHeight = iconHeight
                                    isPreserveRatio = true
                                }
                                label(" ")
                                val timerLabel = label {
                                    addClass(WidgetCss.timerLabel)
                                }
                                object : AnimationTimer() {
                                    override fun handle(now: Long) {
                                        if (model.executionTimeProperty.value != 0L)
                                            timerLabel.text = ofMillis(model.executionTimeProperty.value).format()
                                    }
                                }.start()
                            }
                        }
                    }
                    center {
                        label("        ")
                        spacer { }
                        label("     ")
                    }
                    right {
                        label("  ")
                        vbox {
                            hbox {
                                imageview(resources.image("/icons/clock.png")) {
                                    this.isSmooth = true
                                    addClass(paddedImage2)
                                    fitHeight = iconHeight
                                    isPreserveRatio = true
                                }
                                label(" ")
                                label(model.startTime, converter = date) {
                                    addClass(WidgetCss.startDate)
                                    if (model.startTime.value == 0L) isVisible = false
                                    textProperty().addListener { _, _, _ -> isVisible = true }
                                }
                            }
                            hbox {
                                imageview(resources.image("/icons/clock.png")) {
                                    this.isSmooth = true
                                    addClass(paddedImage2)
                                    fitHeight = iconHeight
                                    isPreserveRatio = true
                                    isVisible = false
                                }
                                label(" ")
                                label(model.startTime, converter = time) {
                                    addClass(WidgetCss.startTime)
                                    if (model.startTime.value == 0L) isVisible = false
                                    textProperty().addListener { _, _, _ -> isVisible = true }
                                }
                            }
                        }
                    }
                }
            }
            hbox {
                label("  ")
                imageview(resources.image("/icons/dna.png")) {
                    this.isSmooth = true
                    addClass(paddedImage2)
                    fitHeight = iconHeight
                    isPreserveRatio = true
                }
                label(" ")
                label(itasser.process.name) {
                    addClass(WidgetCss.sequenceName)
                }
            }

            hbox {
                val ricon = imageview(model.runPauseIcon) {
                    addClass(paddedImage2)
                    addClass("process-widget-run-pause-icon")
                    fitHeight = iconHeight
                    isPreserveRatio = true
                }
                button(graphic = ricon) {
                    addClass(transparentButton)
                    addClass(WidgetCss.controlButton)
                    model.executionStateProperty
                        .addListener { _, _, new -> model.setRunPlayIcon(new) }
                    setOnMouseClicked { processManager.run(itasser) }
                }
                val sicon = imageview(model.stopIcon) {
                    addClass(paddedImage2)
                    this.isSmooth = true
                    fitHeight = iconHeight
                    isPreserveRatio = true
                    spacing = 10.0
                }
                button(graphic = sicon) { addClass(transparentButton); }
            }
        }
        info { "Model user is ${model.item.user}" }
    }
}

val date
    get() = object : StringConverter<Long>() {
        override fun fromString(string: String): Long = 0L
        override fun toString(value: Long?): String =
            DateTime(value).toString("dd / MM / YYYY")
    }

val time
    get() = object : StringConverter<Long>() {
        override fun fromString(string: String): Long = 0L
        override fun toString(value: Long?): String =
            DateTime(value).toString("hh:mm")
    }