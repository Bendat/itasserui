package itasserui.app.viewer.pdb.parser

data class PDB(
    val title: String, val code: String,
    val nodes: List<Atom>, val edges: List<Bond>,
    val structures: List<SecondaryStructure>,
    val residues: List<Residue>
) {
    val oAtoms get() = nodes.filter { it.element == Element.O }
    val cBetaAtoms get() = nodes.filter { it.element == Element.CB }

    val cAlphaCBetaBonds: List<Bond>
        get() = edges.filter {
            it.from.element == Element.CA || it.to.element == Element.CB
        }

    val COBonds: List<Bond>
        get() = edges.filter {
            it.from.element == Element.C || it.to.element == Element.O
        }
}