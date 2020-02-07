package itasserui.lib.pdb.parser

import itasserui.lib.pdb.parser.sections.Header
import itasserui.lib.pdb.parser.sections.Helix
import itasserui.lib.pdb.parser.sections.Sheet

data class PDB(
    val header: Header,
    val nodes: List<Atomic>,
    val edges: List<Bond>,
    val helixStructures: List<SecondaryStructure>,
    val sheetStructures: List<SecondaryStructure>,
    val helices: List<Helix>,
    val sheets: List<Sheet>,
    val residues: List<Residue>
) {
    val oAtoms get() = nodes.filter { it.element == Element.O }
    val cBetaAtoms get() = nodes.filter { it.element == Element.CB }
    val structures: List<SecondaryStructure> get() = helixStructures + sheetStructures
    val cAlphaCBetaBonds: List<Bond>
        get() = edges.filter {
            it.from.element == Element.CA || it.to.element == Element.CB
        }

    val COBonds: List<Bond>
        get() = edges.filter {
            it.from.element == Element.C || it.to.element == Element.O
        }
}