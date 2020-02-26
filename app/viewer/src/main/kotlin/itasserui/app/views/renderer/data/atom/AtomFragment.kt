package itasserui.app.views.renderer.data.atom

import itasserui.lib.pdb.parser.Atom
import itasserui.lib.pdb.parser.NormalizedAtom
import javafx.beans.property.DoubleProperty
import javafx.beans.property.Property
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.ScopedInstance
import tornadofx.group

class AtomFragment(atom: NormalizedAtom, radiusScaling: DoubleProperty, text: String) : Fragment("My View"), ScopedInstance {
    override val scope: Scope = Scope()
    val controller = AtomController(atom, radiusScaling, text, scope)
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
            controller.shapeProperty)

}
