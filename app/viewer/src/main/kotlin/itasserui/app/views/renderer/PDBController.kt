package itasserui.app.views.renderer

import itasserui.app.viewer.pdbmodel.SecondaryStructure
import itasserui.lib.pdb.parser.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

class PDBController(val pdb: PDB) : Controller() {
    val nodes: ObservableList<Atom> = FXCollections.observableArrayList()
    val edges: ObservableList<Bond> = FXCollections.observableArrayList()
    val secondaryStructures: ObservableList<SecondaryStructure> = FXCollections.observableArrayList()
    val residues: ObservableList<Residue> = FXCollections.observableArrayList()

    val codeProperty = pdb.header.code.toProperty()
    var code by codeProperty

    val titleProperty = "".toProperty()
    var title by titleProperty

    val countNode by nodes.sizeProperty
    val countStructures by secondaryStructures.sizeProperty

    val sequence get() = residues.map { it.acid.symbol }

    val cAlphaBetaBonds
        get() = edges
            .filter { it.from.element == Element.CA || it.to.element == Element.CB }
    val cOBonds
        get() = edges
            .filter { it.from.element == Element.C || it.to.element == Element.O }

    val oAtoms get() = nodes.filter { it.element == Element.O }

    val cBetaAtoms get() = nodes.filter { it.element == Element.CB }

    val alphaHelixContent: Map<AminoAcid, Int>
        get() = residues
            .filter { res -> pdb.helixStructures.any { struct -> struct.contains(res) } }
            .groupBy { it.acid }
            .map { it.key to it.value.size }
            .toMap()

    val betaSheetContent: Map<AminoAcid, Int>
        get() = residues
            .filter { res -> pdb.sheetStructures.any { struct -> struct.contains(res) } }
            .groupBy { it.acid }
            .map { it.key to it.value.size }
            .toMap()


}