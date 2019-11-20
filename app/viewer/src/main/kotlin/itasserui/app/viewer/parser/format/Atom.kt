package itasserui.app.viewer.parser.format

import itasserui.app.viewer.parser.PDBParser
import itasserui.app.viewer.parser.PDBParser.getSegment
import itasserui.lib.fasta.AminoAcid
import javafx.scene.paint.Color

fun String.getSegment(start: Int, end: Int) =
    substring(start, end)
        .trim { it <= ' ' }

data class Atom(
    val number: Int,
    val acid: AminoAcid,
    val coordinates: Coordinates,
    val element: ChemicalElement
) {
    val color get() = element.color
    val radius get() = element.radius

    val name get() = "$number\$$acid"

    constructor(line: String) : this(
        number = line.getSegment(22, 27).toInt(),
        acid = AminoAcid.fromAbbreviation(line.getSegment(17, 20)),
        coordinates = Coordinates(
            x = line.getSegment(30, 38).toDouble() * PDBParser.ATOM_DISTANCE_FACTOR,
            y = line.getSegment(38, 46).toDouble() * PDBParser.ATOM_DISTANCE_FACTOR,
            z = line.getSegment(46, 54).toDouble() * PDBParser.ATOM_DISTANCE_FACTOR
        ),
        element = ChemicalElement.valueOf(line.getSegment(12, 16))
    )
}

data class Coordinates(val x: Double, val y: Double, val z: Double)

enum class ChemicalElement(val radius: Double, val color: Color) {
    C(12 / 1.5, Color.web("202020")),
    CA(12 / 1.5, Color.web("202020")),
    CB(12 / 1.5, Color.web("202020")),
    N(14 / 1.5, Color.web("2060ff")),
    O(16 / 1.5, Color.web("ee2010")),
    Other(0.0, Color.TRANSPARENT);

    companion object {
        fun get(value: String) =
            values().firstOrNull { it.name.toLowerCase() == value.toLowerCase() }

        fun any(value: String) = values()
            .any { it.name == value }

    }
}