package itasserui.app.viewer.pdb.parser

import javafx.geometry.Point3D
import javafx.scene.paint.Color

data class Coords(val x: Double, val y: Double, val z: Double)
data class Edges(val incoming: List<Bond>, val outgoing: List<Bond>)
enum class Element(val radius: Double, val color: Color) {
    CA(12 / 1.5, Color.web("202020")),
    CB(12 / 1.5, Color.web("202020")),
    C(12 / 1.5, Color.web("202020")),
    O(16 / 1.5, Color.web("ee2010")),
    N(14 / 1.5, Color.web("2060ff")),
}

data class Atom(
    val position: Point3D,
    val element: Element,
    val sequenceNumber: Int,
    val acid: AminoAcid
) {
    val name get() = "$sequenceNumber$$acid"
}