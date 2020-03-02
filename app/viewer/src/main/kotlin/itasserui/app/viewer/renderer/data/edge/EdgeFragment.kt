package itasserui.app.viewer.renderer.data.edge

import itasserui.app.viewer.renderer.data.atom.AtomFragment
import javafx.beans.property.DoubleProperty
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.ScopedInstance
import tornadofx.group


class EdgeFragment(val from: AtomFragment, val to: AtomFragment, edgeScaling: DoubleProperty) :
    Fragment(), ScopedInstance {
    override val scope: Scope = Scope()
    val controller = EdgeController(from, to, edgeScaling, scope)
    override val root = group {
        userData = this@EdgeFragment
        children += controller.makeLineView().root
    }
}