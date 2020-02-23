package itasserui.app.views.renderer.components.node

import NodeController
import itasserui.app.views.renderer.data.atom.AtomController
import javafx.beans.property.DoubleProperty
import javafx.scene.SubScene
import tornadofx.ItemViewModel

class NodeViewModel(
    atom: AtomController,
    radiusScaling: DoubleProperty,
    subScene: SubScene
) : ItemViewModel<NodeController>(NodeController(atom,
    radiusScaling, subScene)) {
    val shape = bind(NodeController::shapeProperty)
    val material = bind(NodeController::materialProperty)
    val atom = bind(NodeController::atomProperty)
    val radiusScaling = bind(NodeController::radiusScalingProperty)

    init {
        setInScope(item)
    }
}
