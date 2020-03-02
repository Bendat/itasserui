package itasserui.app.viewer.renderer.components.node

import itasserui.app.viewer.renderer.data.atom.AtomController
import javafx.beans.property.DoubleProperty
import javafx.beans.property.Property
import javafx.scene.SubScene
import tornadofx.View
import tornadofx.group

class NodeView(
    atom: AtomController,
    radiusScaling: DoubleProperty,
    subScene: SubScene
) : View("My View") {
    val viewmodel = NodeViewModel(atom, radiusScaling, subScene)

    init {
        setInScope(viewmodel)
    }

    override val root = group {
        children += viewmodel.item.shape
        translateXProperty().bind(atom.xCoordinateProperty)
        translateYProperty().bind(atom.yCoordinateProperty)
        translateZProperty().bind(atom.zCoordinateProperty)
    }

    val viewProperties: Array<Property<*>>
        get() = arrayOf(
            root.translateXProperty(),
            root.translateYProperty(),
            root.translateZProperty(),
            root.scaleXProperty(),
            root.scaleYProperty(),
            root.scaleZProperty(),
            viewmodel.item.shapeProperty)
}
