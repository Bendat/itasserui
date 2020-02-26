package itasserui.app.views.renderer.ui

import itasserui.app.views.renderer.data.atom.AtomFragment
import itasserui.app.views.renderer.data.edge.EdgeFragment
import itasserui.lib.pdb.parser.Atomic
import itasserui.lib.pdb.parser.Bond
import itasserui.lib.pdb.parser.PDB
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue
import tornadofx.toProperty

class PDBController(pdb: PDB) : Controller() {
    val pdbProperty = pdb.toProperty()
    var pdb by pdbProperty

    val titleProperty = pdb.header.title.toProperty()
    var title by titleProperty
    val codeProperty = pdb.header.code.toProperty()
    var code by codeProperty

    val atoms: ObservableList<Atomic> = observableArrayList()
    val edges: ObservableList<Bond> = observableArrayList()

    val helixStuctures = observableArrayList(pdb.helixStructures)
    val sheetStructures = observableArrayList(pdb.sheetStructures)

    val helices = observableArrayList(pdb.helices)
    val sheets = observableArrayList(pdb.sheets)

    val residues = observableArrayList(pdb.residues)
    val stuctures = observableArrayList(pdb.structures)


}