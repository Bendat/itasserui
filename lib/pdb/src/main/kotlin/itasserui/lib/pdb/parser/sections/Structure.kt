package itasserui.lib.pdb.parser.sections


interface Shape {
    val start: Int
    val end: Int
}

data class Helix(override val start: Int, override val end: Int) : Shape
data class Sheet(override val start: Int, override val end: Int) : Shape