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
import org.kodein.di.generic.instance
import tornadofx.*
import java.time.Duration
import kotlin.math.abs


class ProcessWidget(user: User, process: ITasser, scope: Scope? = null) : Fragment("My View"), Logger {
    override val scope: Scope = scope ?: super.scope
    private val iconHeight = 16.0


    val model: ProcessWidgetViewModel = ProcessWidgetViewModel(user, process)

    init {
        setInScope(model, this.scope)
    }

    override val root = anchorpane {
        minWidth = 250.0
        addStylesheet(MainStylee::class)
        vbox {
            hbox {
                label("  ")
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
                                val elapsedMillis = System.currentTimeMillis() - model.startedTime.value.toLong()
                                timerLabel.text = Duration.ofMillis(elapsedMillis).format()
                            }
                        }.start()

                    }
                }
                spacer()
                vbox {
                    hbox {
                        imageview(resources.image("/icons/stopwatch.png")) {
                            this.isSmooth = true
                            addClass(paddedImage2)
                            fitHeight = iconHeight
                            isPreserveRatio = true
                        }
                        label(" ")
                        label(model.startedTimeFormatted) { }
                    }
                }
            }
            hbox {
                label("  ")
                imageview(model.sequenceIcon) {
                    this.isSmooth = true
                    addClass(paddedImage2)
                    fitHeight = iconHeight
                    isPreserveRatio = true
                }
                spacer()
                label(" ")
                label("AMinoOxycillanDehydrateMonoOxide") { addClass(text) }
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
                    setOnMouseReleased {
                        model.togglePlay()
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



