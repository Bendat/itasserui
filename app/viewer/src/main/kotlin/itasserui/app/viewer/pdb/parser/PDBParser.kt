package itasserui.app.viewer.pdb.parser

import arrow.core.*
import arrow.data.Nel
import arrow.data.NonEmptyListOf
import arrow.data.nel
import itasserui.common.errors.RuntimeError
import itasserui.common.extensions.isFalse
import javafx.geometry.Point3D
import javafx.scene.transform.Rotate
import tornadofx.isDouble
import tornadofx.isInt
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

data class Header(val title: String, val code: String)
interface PDBStructure {
    val start: Int
    val end: Int
}

data class Helix(override val start: Int, override val end: Int) : PDBStructure
data class Sheet(override val start: Int, override val end: Int) : PDBStructure
object PDBParser {
    const val atom_distance_factor = 20

    fun parser(file: Path): Either<PDBParseError, PDB> {
        val lines = Files.readAllLines(file)
        val header = getHeader(lines)
        if (header is Either.Left)
            Left(PDBParseError.ParseFailed("Invalid file or header", header.a))
        val helices = getStructure("HELIX", lines, 21 to 26, 33 to 38, 38)
        { s, e -> Helix(s, e) }

        val sheets = getStructure("SHEET", lines, 22 to 27, 33 to 38, 38)
        { s, e -> Sheet(s, e) }
//        if (helices is Either.Left || sheets is Either.Left) {
//            val errors = when {
//                helices is Either.Left && sheets is Either.Left -> helices.a.errors.plus(sheets.a.errors)
//                helices is Either.Left -> helices.a.errors
//                sheets is Either.Left -> sheets.a.errors
//                else -> throw IllegalStateException("[$helices] [$sheets]")
//            }
//            val errorList = PDBParseError.ErrorList(errors)
//            Left(PDBParseError.ParseFailed("Structure parsing failed", errorList))
//        }

        val atoms = getAtoms(lines)
        // TODO error handling
        lateinit var residues: List<Residue>
        atoms.map { atoms -> residues = getResidues(atoms).normalized }
        val nodes: List<Atom> = residues.flatMap { residue -> residue.atoms.toList() }
        lateinit var helixStructures: List<SecondaryStructure>
        lateinit var sheetStructures: List<SecondaryStructure>
        helices.map {
            it.map { helix ->
                helixStructures = getSecondaryStructures(residues, helix, SecondaryStructureType.Alphahelix)
            }
        }
        sheets.map {
            it.map { sheet ->
                sheetStructures = getSecondaryStructures(residues, sheet, SecondaryStructureType.Betasheet)
            }
        }
        val bonds = setupBonds(residues)
        return header.map { header ->
            val structures = helixStructures + sheetStructures
            PDB(header.title, header.code, nodes, bonds, structures, residues)
        }
    }

    fun setupBonds(residues: List<Residue>): ArrayList<Bond> {
        val bonds = arrayListOf<Bond>()
        for (i in 0 until residues.size) {
            val res = residues[i]
            if (i != 0) {
                val from = residues[i - 1].cAtom
                bonds += Bond(from, res.nAtom)
            }
            bonds += Bond(res.nAtom, res.cAlphaAtom)
            bonds += Bond(res.cAlphaAtom, res.cBetaAtom)
            bonds += Bond(res.cAlphaAtom, res.cAtom)
            bonds += Bond(res.cAtom, res.oAtom)

        }

        return bonds
    }

    private val List<Residue>.normalized: List<Residue>
        get() {
            val atoms: List<Atom> = flatMap { it.atoms.toList() }
            val x = atoms.map { it.position.x }.sum() / atoms.size
            val y = atoms.map { it.position.y }.sum() / atoms.size
            val z = atoms.map { it.position.z }.sum() / atoms.size
            fun normalizePosition(position: Point3D): Point3D {
                return Point3D(
                    position.x - x,
                    position.y - y,
                    position.z - z
                )
            }
            return map { residue ->
                val c = residue.cAtom.copy(position = normalizePosition(residue.cAtom.position))
                val ca = residue.cAlphaAtom.copy(position = normalizePosition(residue.cAlphaAtom.position))
                val cb = residue.cBetaAtom.copy(position = normalizePosition(residue.cBetaAtom.position))
                val n = residue.nAtom.copy(position = normalizePosition(residue.nAtom.position))
                val o = residue.oAtom.copy(position = normalizePosition(residue.oAtom.position))
                residue.copy(cAtom = c, cAlphaAtom = ca, cBetaAtom = cb, nAtom = n, oAtom = o)
            }
        }

    fun getResidues(atoms: List<Atom>): List<Residue> {
        return atoms.groupBy { it.sequenceNumber }.map { pair ->
            val caAtom = pair.value.first { it.element == Element.CA }
            val cbAtom = pair.value.first { it.element == Element.CB }
            val cAtom = pair.value.first { it.element == Element.C }
            val nAtom = pair.value.first { it.element == Element.N }
            val oAtom = pair.value.first { it.element == Element.O }
            Residue(pair.key, pair.value.first().acid, cAtom, nAtom, oAtom, caAtom, cbAtom)
        }.map { handleGlycine(it) }
    }

    fun getSecondaryStructures(
        residues: List<Residue>,
        struct: PDBStructure,
        type: SecondaryStructureType
    ): LinkedList<SecondaryStructure> {
        val structures = LinkedList<SecondaryStructure>()
        for (residue in residues) {
            if (structures.peek()?.contains(residue).isFalse
                && residue.sequenceNo == struct.start
            ) {
                val structure = SecondaryStructure(type)
                structure.add(residue)
                structures.push(structure)
            } else {
                structures.peek()?.add(residue)
                if (residue.sequenceNo == struct.end)
                    break

            }
        }
        return structures
    }

    fun handleGlycine(residue: Residue): Residue {
        if (residue.acid != AminoAcid.GLY)
            return residue
        val ca = residue.cAlphaAtom.let { Point3D(it.position.x, it.position.y, it.position.z) }
        val c = residue.cAtom.let { Point3D(it.position.x, it.position.y, it.position.z) }
        val n = residue.nAtom.let { Point3D(it.position.x, it.position.y, it.position.z) }

        val midNC = c.midpoint(n).subtract(ca)
        val nCaCPerpendicular = c.subtract(ca).crossProduct(n.subtract(ca))
        val rotationAxis = nCaCPerpendicular.crossProduct(midNC)
        val rotate = Rotate(120.0, rotationAxis)
        val point = midNC.normalize().multiply(c.subtract(ca).magnitude())
        val result = rotate.transform(point).add(ca)

        val newcBeta = Atom(result, Element.CB, residue.sequenceNo, residue.acid)
        return Residue(
            residue.sequenceNo, residue.acid, residue.cAtom,
            residue.nAtom, residue.oAtom, residue.cAlphaAtom, newcBeta
        )
    }

    fun getAtoms(lines: List<String>): Either<PDBParseError.ErrorList, List<Atom>> {
        val atomLines = lines.filter { it.startsWith("ATOM") }
        val atoms = atomLines.mapNotNull { line ->
            if (54 < line.length)
                Left(PDBParseError.InvalidLine(line, line.length, 54, StructureType.Atom))
            else {
                val atomName = line.getSegment(12, 16)
                if (atomName in Element.values().map { it.name }) {
                    val x = line.getSegment(30, 38)
                    val y = line.getSegment(38, 46)
                    val z = line.getSegment(46, 54)
                    val residueName = line.getSegment(17, 20)
                    val residueSequenceNo = line.getSegment(22, 27)
                    when {
                        !x.isDouble() -> Left(PDBParseError.InvalidCoordinate(line, "x", x))
                        !y.isDouble() -> Left(PDBParseError.InvalidCoordinate(line, "y", y))
                        !z.isDouble() -> Left(PDBParseError.InvalidCoordinate(line, "z", z))
                        else -> Atom(
                            Point3D(
                                x.toDouble() * atom_distance_factor,
                                y.toDouble() * atom_distance_factor,
                                z.toDouble() * atom_distance_factor
                            ),
                            Element.valueOf(atomName), residueSequenceNo.toInt(),
                            AminoAcid.valueOf(residueName)
                        ).right()
                    }
                } else null
            }
        }
        val errors = atoms.filterIsInstance<Either.Left<PDBParseError>>().map { it.a }
        if (errors.isNotEmpty())
            return Left(PDBParseError.ErrorList(Nel(errors.first(), errors.drop(1))))

        return Right(atoms.filterIsInstance<Either.Right<Atom>>().map { it.b })
    }

    private inline fun <reified T : PDBStructure> getStructure(
        prefix: String,
        lines: List<String>,
        start: Pair<Int, Int>,
        end: Pair<Int, Int>,
        length: Int,
        crossinline structure: (Int, Int) -> T
    ): Either<PDBParseError.ErrorList, List<T>> {
        val helices = lines.filter { it.startsWith(prefix) }
        if (helices.any { it.length < length }) {
            val line = helices.first { it.length < 38 }
            val lineError = PDBParseError.InvalidLine(line, line.length, 38, StructureType.Betasheet).nel()
            val errorList = PDBParseError.ErrorList(lineError)
            return Left(errorList)
        }
        val mapped = helices.mapStructure(start, end) { s, e -> structure(s, e) }
        return handleStructureErrors(mapped)
    }


    private fun getHeader(lines: List<String>): Either<PDBParseError, Header> {
        if (lines.isEmpty())
            return Left(PDBParseError.EmptyFile)

        val line = lines.first()
        return if (!line.startsWith("HEADER"))
            Left(PDBParseError.MissingHeader)
        else if (line.length < 66)
            Left(PDBParseError.InvalidLine(line, line.length, 66, StructureType.Header))
        else {
            val title = line.getSegment(10, 50)
            val code = line.getSegment(62, 66)
            Right(Header(title, code))
        }
    }

    private fun String.getSegment(start: Int, end: Int): String {
        return substring(start, end).trim { it <= ' ' }
    }

    private inline fun <reified T> handleStructureErrors(
        mapped: List<Either<PDBParseError, T>>
    ): Either<PDBParseError.ErrorList, List<T>> {
        val errors = mapped.filterIsInstance<Either.Left<PDBParseError>>()
            .map { it.a }
        if (errors.isNotEmpty())
            return PDBParseError.ErrorList(Nel(errors.first(), errors.drop(1))).left()
        return mapped.filterIsInstance<Either.Right<T>>()
            .map { it.b }.right()
    }

    private inline fun <reified T> List<String>.mapStructure(
        startIndices: Pair<Int, Int>,
        endIndices: Pair<Int, Int>,
        structure: (Int, Int) -> T
    ): List<Either<PDBParseError.InvalidSequenceNumber, T>> {
        return map { line ->
            val start = line.getSegment(startIndices.first, startIndices.second)
            val end = line.getSegment(endIndices.first, endIndices.second)
            if (!start.isInt())
                Left(PDBParseError.InvalidSequenceNumber(line, start))
            else if (!end.isInt())
                Left(PDBParseError.InvalidSequenceNumber(line, start))
            else {
                Right(structure(start.toInt(), end.toInt()))
            }
        }
    }
}

sealed class PDBParseError(parent: RuntimeError? = null) : RuntimeError(parent) {
    class ParseFailed(val reason: String, parent: RuntimeError?) : PDBParseError(parent)
    class ErrorList(val errors: Nel<PDBParseError>) : PDBParseError(),
        NonEmptyListOf<PDBParseError> by errors

    object EmptyFile : PDBParseError(null)
    object MissingHeader : PDBParseError(null)
    class InvalidLine(
        val line: String, val size: Int,
        val expectedSize: Int, val status: StructureType,
        parent: RuntimeError? = null
    ) : PDBParseError(parent)

    class InvalidSequenceNumber(val line: String, val number: String, parent: PDBParseError? = null) :
        PDBParseError(parent)

    class InvalidCoordinate(val line: String, val coordinate: String, value: String, parent: PDBParseError? = null) :
        PDBParseError(parent)
}

enum class StructureType {
    Header,
    Remarks,
    Helix,
    Betasheet,
    Atom,
    Term
}