package itasserui.app.views.renderer.atom

import itasserui.app.viewer.pdbmodel.Atom
import javafx.beans.property.DoubleProperty
import tornadofx.ItemViewModel

class NodeViewModel(atom: Atom, radiusScaling: DoubleProperty) :
    ItemViewModel<NodeController>(NodeController(atom, radiusScaling)) {
    val shape = bind(NodeController::shapeProperty)
    val material = bind(NodeController::materialProperty)
    val atom = bind(NodeController::atomProperty)
    val radiusScaling = bind(NodeController::radiusScalingProperty)

    init {
        setInScope(item)
    }
}