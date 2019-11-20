package itasserui.app.viewer.parser.format

import itasserui.lib.fasta.AminoAcid

data class Residue(
    val resNum: String,
    val acid: AminoAcid,
    val atoms: ResidueAtoms,
    val carbon: Carbon,
    val secondaryStructure: SecondaryStructure?
) {
    val contents
        get() = listOf(
            atoms.n, atoms.c,
            carbon.alpha, carbon.beta, atoms.o
        )
    val acidName get() = acid.code
    val name get() = acid.abbreviation
    val structureName get() = secondaryStructure?.code
}

data class ResidueAtoms(
    val c: Atom?,
    val o: Atom?,
    val n: Atom?
)

data class Carbon(val alpha: Atom?, val beta: Atom?)