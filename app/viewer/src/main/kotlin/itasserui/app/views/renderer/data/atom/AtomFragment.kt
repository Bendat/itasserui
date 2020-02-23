package itasserui.app.views.renderer.data.atom

import javafx.beans.property.Property
import tornadofx.*

class AtomFragment(override val scope: Scope) : Fragment("My View"), ScopedInstance {
    val controller: AtomController by inject()
    val model: AtomViewModel by inject()
    override val root = group {
        children += controller.shape
        translateXProperty().bind(controller.xCoordinateProperty)
        translateYProperty().bind(controller.yCoordinateProperty)
        translateZProperty().bind(controller.zCoordinateProperty)
    }

    val viewProperties: Array<Property<*>>
        get() = arrayOf(
            root.translateXProperty(),
            root.translateYProperty(),
            root.translateZProperty(),
            root.scaleXProperty(),
            root.scaleYProperty(),
            root.scaleZProperty(),
            model.item.shapeProperty)
}
