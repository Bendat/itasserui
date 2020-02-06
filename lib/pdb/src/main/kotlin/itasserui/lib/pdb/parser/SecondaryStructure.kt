package itasserui.lib.pdb.parser

enum class SecondaryStructureType {
    Betasheet, Alphahelix, nul;

    override fun toString(): String {
        return name.toLowerCase()
    }

    fun fromString(value: String) = values().first { it.toString() == value.toLowerCase() }
}

class SecondaryStructure(val structureType: SecondaryStructureType) :
    MutableList<Residue> by arrayListOf() {
    val asSymbol get() = if (structureType == SecondaryStructureType.Betasheet) "H" else "E"
    override fun toString(): String {
        return "SecondaryStructure(structureType='$structureType', symbol='$asSymbol', resides='${toList()}')"
    }

}