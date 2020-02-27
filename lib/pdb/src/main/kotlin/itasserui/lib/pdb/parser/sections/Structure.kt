package itasserui.lib.pdb.parser.sections


interface Shape {
    val start: String
    val end: String
}

data class Helix(override val start: String, override val end: String) : Shape
data class Sheet(override val start: String, override val end: String) : Shape