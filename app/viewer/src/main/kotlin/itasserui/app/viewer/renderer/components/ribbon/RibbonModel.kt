package itasserui.app.viewer.renderer.components.ribbon

import tornadofx.ItemViewModel
import tornadofx.Scope

class RibbonModel(controller: RibbonController, override val scope: Scope) :
    ItemViewModel<RibbonController>(controller) {
    val residue = bind(RibbonController::residue)
    val source = bind(RibbonController::sourceProperty)
    val target = bind(RibbonController::targetProperty)
    val mesh = bind(RibbonController::meshProperty)
}