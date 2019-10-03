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
    val kdi: DI by lazy {
        val d = find<DI>()
        d
    }

    override val scope: Scope = scope ?: super.scope
    private val iconHeight = 16.0

    val processManager: ProcessManager by lazy {
        val i by kdi.instance<ProcessManager>()
        i
    }

    val model: ProcessWidgetViewModel = ProcessWidgetViewModel(user, itasser)

    init {
        setInScope(model, this.scope)
    }

    override val root = anchorpane {
        minWidth = 250.0
        addStylesheet(MainStylee::class)
        vbox {
            hbox {
                label("  ")
                borderpane() {
                    left {
                        vbox {
                            hbox {
                                imageview(resources.image("/icons/users.png")) {
                                    addClass(paddedImage2)
                                    fitHeight = iconHeight
                                    isPreserveRatio = true
                                }
                                label(" ")
                                label(model.username) { }
                            }
                            hbox {
                                imageview(resources.image("/icons/clock.png")) {
                                    this.isSmooth = true
                                    addClass(paddedImage2)
                                    fitHeight = iconHeight
                                    isPreserveRatio = true
                                }
                                label(" ")
                                val timerLabel = label { addClass(text) }
                                object : AnimationTimer() {
                                    override fun handle(now: Long) {
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
                        val date = object : StringConverter<Long>() {
                            override fun fromString(string: String): Long = 0L
                            override fun toString(value: Long?): String =
                                DateTime(value).toString("dd / MM / YYYY")
                        }
                        val time = object : StringConverter<Long>() {
                            override fun fromString(string: String): Long = 0L
                            override fun toString(value: Long?): String =
                                DateTime(value).toString("HH:MM")
                        }
                        label("  ")

                        vbox {
                            hbox {
                                imageview(resources.image("/icons/stopwatch.png")) {
                                    this.isSmooth = true
                                    addClass(paddedImage2)
                                    fitHeight = iconHeight
                                    isPreserveRatio = true
                                }
                                label(" ")
                                label(model.startTime, converter = date)
                            }
                            label(model.startTime, converter = time)
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
                label(itasser.process.name)
            }

            hbox {
                val ricon = imageview(model.runPauseIcon) {
                    addClass(paddedImage2)
                    fitHeight = iconHeight
                    isPreserveRatio = true
                }
                button(graphic = ricon) {
                    addClass(transparentButton)
                    addClass(text)
                    model.executionStateProperty
                        .addListener { _, _, new -> model.setRunPlayIcon(new) }
                    setOnMouseClicked {
                        processManager.run(itasser)
                    }
                }
                val sicon = imageview(model.stopIcon) {
                    addClass(paddedImage2)
                    this.isSmooth = true
                    fitHeight = iconHeight
                    isPreserveRatio = true
                    spacing = 10.0
                }
                button(graphic = sicon) { addClass(transparentButton); addClass(text) }
            }
        }
        info { "Model user is ${model.item.user}" }
    }
}




