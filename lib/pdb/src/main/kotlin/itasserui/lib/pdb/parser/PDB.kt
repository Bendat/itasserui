package itasserui.lib.pdb.parser

import itasserui.lib.pdb.parser.sections.EmptyHeader
import itasserui.lib.pdb.parser.sections.Header
import itasserui.lib.pdb.parser.sections.Helix
import itasserui.lib.pdb.parser.sections.Sheet

interface ParsedPDB {
    val header: Header
    val nodes: List<Atomic>
    val edges: List<Bond>
    val helixStructures: List<SecondaryStructure>
    val sheetStructures: List<SecondaryStructure>
    val helices: List<Helix>
    val sheets: List<Sheet>
    val residues: List<Residue>

    val oAtoms get() = nodes.filter { it.element == Element.O }
    val cBetaAtoms get() = nodes.filter { it.element == Element.CB }
    val structures: List<SecondaryStructure> get() = helixStructures + sheetStructures
    val cAlphaCBetaBonds: List<Bond>
        get() = edges
            .filter { it.from.element == Element.CA || it.to.element == Element.CB }

    val COBonds: List<Bond>
        get() = edges
            .filter { it.from.element == Element.C || it.to.element == Element.O }

    val sequence get() = residues.joinToString("") { it.acid.symbol }

    val helixContent
        get() = helixStructures
            .flatMap { s -> s.map { r -> r.acid } }
            .groupingBy { it }
            .eachCount()

    val sheetContent: Map<AminoAcid, Int>
        get() = sheetStructures
            .flatMap { s -> s.map { r -> r.acid } }
            .groupingBy { it }
            .eachCount()


}

data class PDB(
    override val header: Header,
    override val nodes: List<Atomic>,
    override val edges: List<Bond>,
    override val helixStructures: List<SecondaryStructure>,
    override val sheetStructures: List<SecondaryStructure>,
    override val helices: List<Helix>,
    override val sheets: List<Sheet>,
    override val residues: List<Residue>
) : ParsedPDB

object UninitializedPDB : ParsedPDB {
    override val header: Header = EmptyHeader
    override val nodes: List<Atomic> = listOf()
    override val edges: List<Bond> = listOf()
    override val helixStructures: List<SecondaryStructure> = listOf()
    override val sheetStructures: List<SecondaryStructure> = listOf()
    override val helices: List<Helix> = listOf()
    override val sheets: List<Sheet> = listOf()
    override val residues: List<Residue> = listOf()
}