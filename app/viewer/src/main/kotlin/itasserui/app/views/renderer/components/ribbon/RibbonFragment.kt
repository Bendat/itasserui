package itasserui.app.views.renderer.components.ribbon

import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.ScopedInstance
import tornadofx.group

class RibbonFragment(override val scope: Scope) : Fragment(), ScopedInstance {
    val controller: RibbonController by inject()
    val model: RibbonModel by inject()
    override val root = group {}
}