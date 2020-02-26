package itasserui.app.views.renderer.components.ribbon

import itasserui.lib.pdb.parser.Residue
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.ScopedInstance
import tornadofx.group

class RibbonFragment(val residue: Residue) : Fragment(), ScopedInstance {
    override val scope: Scope = Scope()
    val controller = RibbonController(residue, scope)
    override val root = group {}

    init {
        setInScope(this, scope)
        setInScope(controller, scope)
        controller.load(root.children)
    }

}