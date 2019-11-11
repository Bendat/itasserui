package itasser.app.mytasser.views

import tornadofx.View
import tornadofx.anchorpane
import tornadofx.label
import tornadofx.splitpane

class MainView : View("Hello TornadoFX") {
    override val root = splitpane {
        anchorpane {
            prefHeight = 500.0
            prefWidth = 250.0
            label("Hello")
        }

        anchorpane {
            prefHeight = 500.0
            prefWidth = 250.0
            label("Hello")
        }

        anchorpane {
            prefHeight = 500.0
            prefWidth = 250.0
            label("Hello")
        }
    }
}