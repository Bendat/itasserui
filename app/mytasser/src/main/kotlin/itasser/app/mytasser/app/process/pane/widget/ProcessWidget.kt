package itasser.app.mytasser.app.process.pane.widget

import itasserui.common.logger.Logger
import tornadofx.*

class ProcessWidget : View("My View"), Logger {
    private val iconHeight = 16.0
    val model: ProcessWidgetViewModel by inject()
    override val root = anchorpane {
        vbox {
            hbox {
                minWidth = 250.0
                imageview(model.userIcon) {
                    fitHeight = iconHeight
                    isPreserveRatio = true
                }
                label("User.name") { }
                spacer()
                imageview(model.startedTimeIcon) {
                    fitHeight = iconHeight
                    isPreserveRatio = true
                }
                label(model.startedTime) { }
                spacer()

            }
            hbox {
                imageview(model.sequenceIcon){
                    fitHeight = iconHeight
                    isPreserveRatio = true
                }
                spacer()
                label("AMinoOxycillanDehydrateMonoOxide")
            }
            hbox {
                val ricon = imageview(model.runPauseIcon) {
                    fitHeight = iconHeight
                    isPreserveRatio = true
                }
                button(graphic = ricon)
                spacer()
                val sicon = imageview(model.stopIcon) {
                    fitHeight = iconHeight
                    isPreserveRatio = true
                }
                button(graphic = sicon) { }
            }
        }
    }
}
