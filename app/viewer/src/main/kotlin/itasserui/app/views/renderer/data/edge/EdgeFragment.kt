package itasserui.app.views.renderer.data.edge

import itasserui.app.views.renderer.data.atom.AtomFragment
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
        children += controller.makeLineView().root
    }
}