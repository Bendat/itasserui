package itasser.app.mytasser.app.mainview

import itasser.app.mytasser.app.mainview.tabpane.SplitTabPane
import itasser.app.mytasser.app.process.pane.ProcessPane
import tornadofx.*

class SplitView(scope: Scope? = null) : View("Hello TornadoFX") {
    override val scope = scope ?: super.scope
    override val root = splitpane {
        prefWidth = 600.0
        setDividerPositions(0.4, 1.0, 2.0)
        this += ProcessPane(scope)
        this += SplitTabPane(scope)
        label("Hello")
    }
}