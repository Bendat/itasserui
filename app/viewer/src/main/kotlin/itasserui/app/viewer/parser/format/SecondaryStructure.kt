package itasserui.app.viewer.parser.format

data class SecondaryStructure(
    val type: StructureType,
    val residues: List<Residue> = listOf()
) {
    val size get() = residues.size
    val first get() = residues.first()
    val last get() = residues.last()

    val code get() = type.code
}

enum class StructureType(val code: String) {
    BetaSheet("E"),
    AlphaHelix("H")
}