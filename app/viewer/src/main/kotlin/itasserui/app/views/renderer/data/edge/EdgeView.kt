package itasserui.app.views.renderer.data.edge

import tornadofx.Scope
import tornadofx.View
import tornadofx.group


class EdgeView(override val scope: Scope) : View() {
    val model by inject<EdgeViewModel>()
    val controller by inject<EdgeController>()
    override val root = group {
    }
}