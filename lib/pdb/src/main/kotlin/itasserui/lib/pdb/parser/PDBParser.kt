package itasserui.lib.pdb.parser

import arrow.core.Either
import arrow.core.Left
import arrow.core.right
import arrow.data.*
import itasserui.common.extensions.isTrue
import itasserui.common.logger.Logger
import itasserui.lib.pdb.parser.errors.*
import itasserui.lib.pdb.parser.sections.*
import javafx.geometry.Point3D
import javafx.scene.transform.Rotate
import tornadofx.isDouble
import tornadofx.isInt
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

enum class ShapeType {
    HELIX,
    SHEET
}

object PDBParser : Logger {
    const val atom_distance_factor = 20

    @Suppress("NAME_SHADOWING")
    fun parse(file: Path): Validated<PDBParseError, PDB> {
        lateinit var residues: List<Residue>
        lateinit var helixStructures: List<SecondaryStructure>
        lateinit var sheetStructures: List<SecondaryStructure>

        val lines = Files.readAllLines(file)
        // todo: validate lines
        val header: Header = getHeader(lines).fold({ EmptyHeader }) { it }

        val helices = getStructure(ShapeType.HELIX, lines, 21 to 26, 33 to 38, 38)
        { s, e -> Helix(s, e) }
        val sheets = getStructure(ShapeType.SHEET, lines, 22 to 27, 33 to 38, 38)
        { s, e -> Sheet(s, e) }

        val atoms = getAtoms(lines)

        atoms.map { atoms -> residues = getResidues(atoms).normalized }
        val nodes: List<Atomic> = residues.flatMap { residue ->
            residue.atoms.sortedBy { it.line }.filterNot { it.line == -1 }.toList()
        }

        helices.map { helixStructures = getShape(it, Alphahelix, residues) }
        print("Sheets are $sheets")
        sheets.map { sheetStructures = getShape(it, Betasheet, residues) }

        val bonds = setupBonds(residues)
        val helixList = helices.fold({ listOf<Helix>() }) { it }
        val sheetList = sheets.fold({ listOf<Sheet>() }) { it }

        return PDB(header, nodes, bonds, helixStructures, sheetStructures,
            helixList, sheetList, residues).valid()
    }

    private fun getShape(
        shapes: List<Shape>,
        type: SecondaryStructureType,
        residues: List<Residue>
    ): List<SecondaryStructure> =
        shapes.flatMap { getSecondaryStructures(residues, it, type) }


    fun setupBonds(residues: List<Residue>): ArrayList<Bond> {
        val bonds = arrayListOf<Bond>()
        for (i in residues.indices) {
            val res = residues[i]

            if (i != 0)
                bonds += Bond(residues[i - 1].cAtom, res.nAtom)

            bonds += Bond(res.nAtom, res.cAlphaAtom)
            bonds += Bond(res.cAlphaAtom, res.cBetaAtom)
            bonds += Bond(res.cAlphaAtom, res.cAtom)
            bonds += Bond(res.cAtom, res.oAtom)
        }
        return bonds
    }

    private val List<Residue>.normalized: List<Residue>
        get() {
            val atoms: List<Atomic> = flatMap { it.atoms.toList() }

            fun average(property: (Atomic) -> Double) =
                atoms.map { property(it) }.sum() / atoms.size

            val x = average { it.position.x }
            val y = average { it.position.y }
            val z = average { it.position.z }

            fun normalizePosition(position: Point3D): Point3D =
                Point3D(position.x - x, position.y - y, position.z - z)

            fun copyOrIgnore(atom: Atomic): Atomic = when (atom) {
                is Atom -> atom.normalized(normalizePosition(atom.position))
                is NormalizedAtom -> atom
                is EmptyAtom -> EmptyAtom
            }

            return map { residue ->
                val c = copyOrIgnore(residue.cAtom)
                val ca = copyOrIgnore(residue.cAlphaAtom)
                val cb = copyOrIgnore(residue.cBetaAtom)
                val n = copyOrIgnore(residue.nAtom)
                val o = copyOrIgnore(residue.oAtom)
                residue.copy(cAtom = c, cAlphaAtom = ca, cBetaAtom = cb, nAtom = n, oAtom = o)
            }
        }

    private fun getResidues(atoms: List<Atomic>): List<Residue> {
        return atoms.groupBy { it.sequenceNumber }.map { pair ->
            val caAtom = pair.value.firstOrNull { it.element == Element.CA } ?: EmptyAtom
            val cbAtom = pair.value.firstOrNull { it.element == Element.CB } ?: EmptyAtom
            val cAtom = pair.value.firstOrNull { it.element == Element.C } ?: EmptyAtom
            val nAtom = pair.value.firstOrNull { it.element == Element.N } ?: EmptyAtom
            val oAtom = pair.value.firstOrNull { it.element == Element.O } ?: EmptyAtom
            Residue(pair.key, pair.value.first().acid, cAtom, nAtom, oAtom, caAtom, cbAtom)
        }.map { handleGlycine(it) }
    }

    internal fun getSecondaryStructures(
        residues: List<Residue>,
        struct: Shape,
        type: SecondaryStructureType
    ): LinkedList<SecondaryStructure> {
        val structures = LinkedList<SecondaryStructure>()
        for (residue in residues) {
            if (structures.peek()?.contains(residue).isTrue) {
                structures.peek()?.add(residue)
                if (residue.sequenceNo == struct.end)
                    break
            }
            if (residue.sequenceNo == struct.start)
                continue
            val structure = SecondaryStructure(type)
            structure.add(residue)
            structures.push(structure)
        }
        return structures
    }

    private fun handleGlycine(residue: Residue): Residue {
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

        val newcBeta =
            Atom(result, Element.CB, residue.sequenceNo, residue.acid, residue.cBetaAtom.line)

        return Residue(residue.sequenceNo, residue.acid, residue.cAtom,
            residue.nAtom, residue.oAtom, residue.cAlphaAtom, newcBeta)
    }

    internal fun getAtoms(lines: List<String>): Validated<ErrorList, List<Atom>> {
        val atomLines = lines.filter { it.startsWith("ATOM") }
        val atoms = atomLines.mapIndexedNotNull { number, line ->
            when {
                54 > line.length -> Left(InvalidLine(line, line.length, 54, StructureType.Atom))
                else -> {
                    val atomName = line.getSegment(12, 16)
                    if (atomName !in Element.values().map { it.name }) null
                    else getAtomFromLine(line, atomName, number)
                }
            }
        }
        val errors = atoms
            .filterIsInstance<Either.Left<PDBParseError>>()
            .map { it.a }

        if (errors.isNotEmpty())
            return Invalid(ErrorList(Nel(errors.first(), errors.drop(1))))

        return Valid(atoms.filterIsInstance<Either.Right<Atom>>().map { it.b })
    }

    private fun getAtomFromLine(
        line: String,
        atomName: String,
        number: Int
    ): Either<InvalidCoordinate, Atom> {
        val x = line.getSegment(30, 38)
        val y = line.getSegment(38, 46)
        val z = line.getSegment(46, 54)
        val residueName = line.getSegment(17, 20)
        val residueSequenceNo = line.getSegment(22, 27)
        return when {
            !x.isDouble() -> Left(InvalidCoordinate(line, "x", x))
            !y.isDouble() -> Left(InvalidCoordinate(line, "y", y))
            !z.isDouble() -> Left(InvalidCoordinate(line, "z", z))
            else -> Atom(
                Point3D(x.toDouble() * atom_distance_factor,
                    y.toDouble() * atom_distance_factor,
                    z.toDouble() * atom_distance_factor),
                Element.valueOf(atomName), residueSequenceNo.toInt(),
                AminoAcid.valueOf(residueName),
                number
            ).right()
        }
    }

    private inline fun <reified T : Shape> getStructure(
        prefix: ShapeType,
        lines: List<String>,
        start: Pair<Int, Int>,
        end: Pair<Int, Int>,
        length: Int,
        crossinline structure: (Int, Int) -> T
    ): Validated<ErrorList, List<T>> {
        val helices = lines.filter { it.startsWith(prefix.name) }
        if (helices.none { it.length < length }) {
            val mapped = helices.mapStructure(start, end) { s, e -> structure(s, e) }
            return handleStructureErrors(mapped)
        }
        val line = helices.first { it.length < 38 }
        val lineError = InvalidLine(line, line.length, 38, StructureType.Betasheet).nel()
        val errorList = ErrorList(lineError)
        return Invalid(errorList)
    }


    private fun getHeader(lines: List<String>): Validated<PDBParseError, Header> {
        val line = lines.first()
        return when {
            !line.startsWith("HEADER") -> Valid(EmptyHeader)
            line.length < 66 ->
                Invalid(InvalidLine(line, line.length, 66, StructureType.Header))
            else -> {
                val title = line.getSegment(10, 50)
                val code = line.getSegment(62, 66)
                Valid(ValidHeader(title, code))
            }
        }
    }

    private fun String.getSegment(start: Int, end: Int): String {
        return substring(start, end).trim { it <= ' ' }
    }

    private inline fun <reified T> handleStructureErrors(
        mapped: List<Validated<PDBParseError, T>>
    ): Validated<ErrorList, List<T>> {
        val errors = mapped.filterIsInstance<Invalid<PDBParseError>>()
            .map { it.e }
        if (errors.isNotEmpty())
            return ErrorList(Nel(errors.first(), errors.drop(1))).invalid()
        return mapped.filterIsInstance<Valid<T>>()
            .map { it.a }.valid()
    }

    private inline fun <reified T> List<String>.mapStructure(
        startIndices: Pair<Int, Int>,
        endIndices: Pair<Int, Int>,
        structure: (Int, Int) -> T
    ): List<Validated<InvalidSequenceNumber, T>> {
        return map { line ->
            val start = line.getSegment(startIndices.first, startIndices.second)
            val end = line.getSegment(endIndices.first, endIndices.second)
            when {
                !start.isInt() -> Invalid(InvalidSequenceNumber(line, start))
                !end.isInt() -> Invalid(InvalidSequenceNumber(line, start))
                else -> Valid(structure(start.toInt(), end.toInt()))
            }
        }
    }
}


@Suppress("unused")
enum class StructureType {
    Header,
    Remarks,
    Helix,
    Betasheet,
    Atom,
    Term
}