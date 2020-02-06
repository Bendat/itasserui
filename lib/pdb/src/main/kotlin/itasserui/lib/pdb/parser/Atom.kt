package itasserui.lib.pdb.parser

import javafx.geometry.Point3D
import javafx.scene.paint.Color

data class Edges(val incoming: List<Bond>, val outgoing: List<Bond>)

enum class Element(val radius: Double, val color: Color) {
    CA(12 / 1.5, Color.web("202020")),
    CB(12 / 1.5, Color.web("202020")),
    C(12 / 1.5, Color.web("202020")),
    O(16 / 1.5, Color.web("ee2010")),
    N(14 / 1.5, Color.web("2060ff")),
    @Suppress("DIVISION_BY_ZERO")
    NUL(1 / 0.0, Color.BLACK)
}

sealed class Atomic {
    abstract val position: Point3D
    abstract val element: Element
    abstract val sequenceNumber: Int
    abstract val acid: AminoAcid
    abstract val line: Int
}

val Atomic.asAtom get() = Atom(position, element, sequenceNumber, acid, line)

data class Atom(
    override val position: Point3D,
    override val element: Element,
    override val sequenceNumber: Int,
    override val acid: AminoAcid,
    override val line: Int
) : Atomic() {
    val name get() = "$sequenceNumber$$acid"

    fun normalized(position: Point3D) =
        NormalizedAtom(position, element, sequenceNumber, acid, line, this)
}

data class NormalizedAtom(
    override val position: Point3D,
    override val element: Element,
    override val sequenceNumber: Int,
    override val acid: AminoAcid,
    override val line: Int,
    val old: Atomic
) : Atomic() {
    val name get() = "$sequenceNumber$$acid"

    constructor(atom: Atom) : this(atom.position, atom.element,
        atom.sequenceNumber, atom.acid, atom.line, atom)
}

object EmptyAtom : Atomic() {
    override val position: Point3D get() = Point3D(0.0, 0.0, 0.0)
    override val element: Element get() = Element.NUL
    override val sequenceNumber: Int get() = -100
    override val acid: AminoAcid get() = AminoAcid.NUL
    override val line: Int = -1
}