package itasserui.app.viewer.pdb.parser

enum class SecondaryStructureType {
    Betasheet, Alphahelix;

    override fun toString(): String {
        return name.toLowerCase()
    }

    fun fromString(value: String) = values().first { it.toString() == value.toLowerCase() }
}

class SecondaryStructure(val structureType: SecondaryStructureType) :
    MutableList<Residue> by arrayListOf() {
    val asSymbol get() = if (structureType == SecondaryStructureType.Betasheet) "H" else "E"
}