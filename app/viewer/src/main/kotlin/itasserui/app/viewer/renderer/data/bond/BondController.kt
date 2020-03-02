package itasserui.app.viewer.renderer.data.bond

import itasserui.lib.pdb.parser.Bond
import tornadofx.Controller
import tornadofx.*

class BondController(bond: Bond): Controller() {
    val sourceProperty = bond.from.toProperty()
    var source by sourceProperty

    val targetProperty = bond.to.toProperty()
    var target by targetProperty

    val weightProperty = 0.0.toProperty()
    var weight by weightProperty

    val textProperty = "".toProperty()
    var text by textProperty
}