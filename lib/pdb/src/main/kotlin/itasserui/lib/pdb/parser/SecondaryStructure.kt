package itasserui.lib.pdb.parser

sealed class SecondaryStructureType {
    override fun toString(): String {
        return this::class.simpleName?.toLowerCase() ?: throw IllegalStateException()
    }

    fun fromString(value: String) = values.first { it.toString() == value.toLowerCase() }

    companion object {
        val values
            get() = SecondaryStructureType::class
                .sealedSubclasses
                .map { it.objectInstance }
    }
}

object Betasheet : SecondaryStructureType()
object Alphahelix : SecondaryStructureType()

class SecondaryStructure(val structureType: SecondaryStructureType) :
    MutableList<Residue> by arrayListOf() {
    val asSymbol get() = if (structureType is Betasheet) "H" else "E"
    override fun toString(): String {
        return "SecondaryStructure(structureType='$structureType', symbol='$asSymbol', resides='${toList()}')"
    }

}