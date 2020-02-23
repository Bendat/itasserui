package itasserui.app.views.renderer.data.edge

import tornadofx.ItemViewModel
import tornadofx.Scope

class EdgeViewModel(controller: EdgeController, override val scope: Scope) :
    ItemViewModel<EdgeController>(controller) {
    val source = bind(EdgeController::source)
    val target = bind(EdgeController::target)
    val color = bind(EdgeController::color)
    val line = bind(EdgeController::line)
    val radius = bind(EdgeController::radiusProperty)
}