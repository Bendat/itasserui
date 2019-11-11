package itasser.app.mytasser.app.mainview.tabpane

import tornadofx.View
import tornadofx.tab
import tornadofx.tabpane

class MainViewTabPane : View("ITasser Tab View") {
    override val root = tabpane {
        tab("Hello"){

        }

        tab("World")
    }
}