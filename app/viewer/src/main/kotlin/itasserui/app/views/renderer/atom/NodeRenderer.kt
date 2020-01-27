package itasserui.app.views.renderer.atom

import itasserui.app.viewer.pdbmodel.Atom
import javafx.beans.property.DoubleProperty
import tornadofx.View
import tornadofx.group

class NodeRenderer(atom: Atom, radiusScaling: DoubleProperty) : View("My View") {
    val viewmodel = NodeViewModel(atom, radiusScaling)

    init {
        setInScope(viewmodel)
    }

    override val root = group {
        children += viewmodel.item.shape
        translateXProperty().bind(atom.xCoordinateProperty)
        translateYProperty().bind(atom.yCoordinateProperty)
        translateZProperty().bind(atom.zCoordinateProperty)
    }
}
