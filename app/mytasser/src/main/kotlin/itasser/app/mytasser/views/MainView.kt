package itasser.app.mytasser.views

import itasser.app.mytasser.app.mainview.consoletab.ConsoleView
import itasser.app.mytasser.app.process.pane.ProcessPane
import tornadofx.*

class MainView(scope: Scope? = null) : View("Hello TornadoFX") {
    override val scope = scope ?: super.scope
    override val root = splitpane {
        anchorpane {
            prefHeight = 500.0
            prefWidth = 250.0
            this += ProcessPane(scope)
        }

        anchorpane {
            prefHeight = 500.0
            prefWidth = 250.0
            this += ConsoleView(scope)
        }

        anchorpane {
            prefHeight = 500.0
            prefWidth = 250.0
            label("Hello")
        }
    }
}