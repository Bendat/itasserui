package itasser.app.mytasser.app.mainview.tabpane

import itasser.app.mytasser.app.mainview.consoletab.ConsoleView
import tornadofx.*

class MainViewTabPane(scope: Scope? = null) : View("ITasser Tab View") {
    override val scope = scope ?: super.scope
    override val root = tabpane {
        maxWidth = Double.MAX_VALUE
        prefWidth = 500.0
        tab("Sequence") {
            addChildIfPossible(ConsoleView(scope).root)
        }

        tab("3D Viewer")
    }
}