package itasserui.app.viewer.parser

import itasserui.app.viewer.parser.format.*
import itasserui.common.logger.Logger
import itasserui.lib.fasta.AminoAcid
import java.nio.file.Files
import java.nio.file.Path

enum class Category {
    Header,
    Remarks,
    Helix,
    BetaSheet,
    Atom,
    Term
}

object PDBParser : Logger {
    val ATOM_DISTANCE_FACTOR = 20

    fun parse(file: Path) {
        val outEdges = arrayListOf<Bond>()
        val inEdges = arrayListOf<Bond>()
        val lines = Files.readAllLines(file)
        val headers = lines.filter { it.startsWith("HEADER") }
            .map { Header(it) }
        val betaSheets = lines.filter { it.startsWith("SHEET") }
            .map { BetaSheet(it) }g
        val helices = lines.filter { it.startsWith("HELIX") }
            .map { Helix(it) }
        val atoms = getAtoms(lines, outEdges, inEdges)
        val residueAtoms: Map<Int, ResidueAtoms> = getResidueAtoms(atoms)
        val carbons = getCarbons(atoms)

    }

    interface PDBLine
    data class BetaSheet(val start: Int, val end: Int) : PDBLine {
        constructor(stringForm: String) :
                this(
                    stringForm.getSegment(22, 27).toInt(),
                    stringForm.getSegment(33, 38).toInt()
                )
    }

    data class Helix(val start: Int, val end: Int) : PDBLine {
        constructor(stringForm: String) :
                this(
                    stringForm.getSegment(21, 26).toInt(),
                    stringForm.getSegment(33, 38).toInt()
                )
    }

    data class Header(val title: String, val code: String) : PDBLine {
        constructor(stringForm: String) :
                this(
                    stringForm.getSegment(10, 50),
                    stringForm.getSegment(62, 66)
                )
    }

    private fun getCarbons(atoms: List<Atom>): Map<Int, Carbon> {
        return atoms.groupBy { it.number }.map {
            fun findAtom(element: ChemicalElement) =
                it.value.firstOrNull { atom -> atom.element == element }
            it.key to Carbon(
                alpha = findAtom(ChemicalElement.CA),
                beta = findAtom(ChemicalElement.CB)
            )
        }.toMap()
    }

    private fun getResidueAtoms(atoms: List<Atom>): Map<Int, ResidueAtoms> {
        return atoms.groupBy { it.number }.map {
            fun findAtom(element: ChemicalElement) =
                it.value.firstOrNull { atom -> atom.element == element }

            it.key to ResidueAtoms(
                c = findAtom(ChemicalElement.C),
                o = findAtom(ChemicalElement.O),
                n = findAtom(ChemicalElement.N)
            )
        }.toMap()
    }

    private fun getAtoms(
        lines: MutableList<String>,
        outEdges: ArrayList<Bond>,
        inEdges: ArrayList<Bond>
    ): List<Atom> {
        return lines.filter { it.startsWith("ATOM") }
            .filter { ChemicalElement.any(it.getSegment(12, 16)) }
            .map {
                val name = ChemicalElement.valueOf(it.getSegment(12, 16))
                val x = it.getSegment(30, 38).toDouble() * ATOM_DISTANCE_FACTOR
                val y = it.getSegment(38, 46).toDouble() * ATOM_DISTANCE_FACTOR
                val z = it.getSegment(46, 54).toDouble() * ATOM_DISTANCE_FACTOR

                val residueName = AminoAcid.fromAbbreviation(it.getSegment(17, 20))
                val resNum = it.getSegment(22, 27).toInt()
                Atom(
                    resNum, residueName,
                    Coordinates(x, y, z),
                    name
                )
            }
    }

    fun processLine() {

    }

    fun String.getSegment(start: Int, end: Int) =
        substring(start, end)
            .trim { it <= ' ' }
}