package itasserui.app.viewer.parser.format

data class Bond(
    val text: String,
    val source: Atom,
    val target: Atom,
    val weight: Double
)