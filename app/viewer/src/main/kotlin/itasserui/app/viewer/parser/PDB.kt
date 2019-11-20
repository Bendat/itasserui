package itasserui.app.viewer.parser

import itasserui.app.viewer.parser.format.*
import itasserui.app.viewer.parser.format.ChemicalElement.*
import itasserui.lib.fasta.AminoAcid

data class PDB(
    val title: String,
    val code: String,
    val nodes: List<Atom>,
    val edges: List<Bond>,
    val residues: List<Residue>,
    val stuctures: List<SecondaryStructure>
) {

    val sequence = StringBuilder().apply {
        residues.forEach {
            append(it.acidName)
        }
    }.toString()

    val bonds = Bonds(
        edges.filter { it.source.element == CA && it.target.element == CB },
        edges.filter { it.source.element == C && it.target.element == O }
    )


    val atoms = Atoms(
        nodes.filter { it.element == O },
        nodes.filter { it.element == CB }
    )
    val content = Content(
        getHelix(StructureType.AlphaHelix),
        getHelix(StructureType.BetaSheet),
        getHelix(null)
    )

    private fun getHelix(structureType: StructureType?): Map<AminoAcid, Int> {
        return residues.filter { it.secondaryStructure?.type == structureType }
            .groupingBy { it.acid }
            .eachCount()
    }
}

data class Bonds(
    val alphaBeta: List<Bond>,
    val CO: List<Bond>
)

data class Atoms(
    val o: List<Atom>,
    val cBeta: List<Atom>
)

data class Content(
    val alphaHelix: Map<AminoAcid, Int>,
    val bethaSheet: Map<AminoAcid, Int>,
    val coil: Map<AminoAcid, Int>
)
