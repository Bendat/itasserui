package itasser.app.mytasser.views

import itasser.app.mytasser.app.mainview.consoletab.ConsoleView
import itasser.app.mytasser.app.mainview.tabpane.MainViewTabPane
import itasser.app.mytasser.app.process.pane.ProcessPane
import tornadofx.*

class MainView(scope: Scope? = null) : View("Hello TornadoFX") {
    override val scope = scope ?: super.scope
    override val root = splitpane {
        prefWidth = 600.0
        setDividerPositions(0.4, 1.0, 2.0)
        this += ProcessPane(scope)

        this += MainViewTabPane(scope)

        label("Hello")
    }
}