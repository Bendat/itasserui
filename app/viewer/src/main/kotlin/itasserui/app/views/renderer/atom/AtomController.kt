package itasserui.app.views.renderer.atom

import itasserui.app.viewer.pdbmodel.Bond
import itasserui.lib.pdb.parser.Atom
import itasserui.lib.pdb.parser.Residue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.getValue
import tornadofx.setValue
import tornadofx.toProperty

class AtomController(atom: Atom, residue: Residue, text: String) {
    val outEdges: ObservableList<Bond> = FXCollections.observableArrayList()
    val inEdges: ObservableList<Bond> = FXCollections.observableArrayList()

    val atomProperty = atom.toProperty()
    var atom by atomProperty

    val residueProperty = residue.toProperty()

    val textProperty = text.toProperty()
    var text by textProperty

    val xCoordinateProperty = atom.position.x.toProperty()
    var xCoordinate by xCoordinateProperty
    val yCoordinateProperty = atom.position.y.toProperty()
    var yCoordinate by yCoordinateProperty
    val zCoordinateProperty = atom.position.z.toProperty()
    var zCoordinate by zCoordinateProperty

    val colorProperty = atom.element.color.toProperty()
    var color by colorProperty

    val radiusProperty = atom.element.radius
    var radius by residueProperty
}