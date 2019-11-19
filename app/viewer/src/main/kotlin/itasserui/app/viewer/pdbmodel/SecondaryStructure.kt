package itasserui.app.viewer.pdbmodel

import java.util.ArrayList

/**
 * Secondary Structure representation.
 */
class SecondaryStructure {

    /**
     * Residues contained by a secondary structure.
     */
    /**
     * Get a list of the contained residues.
     * @return List of residues contained in this secondary structure.
     */
    var residuesContained: ArrayList<Residue> = arrayListOf()

    /**
     * The secondaryStructureType of the secondary structure.
     */
    /**
     * Get the type of the secondary structure.
     * @return Type of the secondary structure.
     */
    var secondaryStructureType: StructureType? = null
        private set

    /**
     * Get the length of the secondary structure element.
     *
     * @return length of the secondary structure element.
     */
    val length: Int
        get() = residuesContained.size

    /**
     * Get a one letter type of this SecondaryStructure instance. H for Helix, E for beta sheet.
     * @return H for helix, E for beta sheet.
     */
    internal// alphahelix
    // always betasheet
    val oneLetterSecondaryStructureType: String
        get() = if (this.secondaryStructureType!!.toString() == StructureType.alphahelix.toString()) {
            "H"
        } else {
            "E"
        }

    /**
     * Get the first residue contained in the secondary structure.
     * @return The first residue contained in the secondary structure.
     */
    val firstResidue: Residue
        get() = residuesContained!![0]

    /**
     * Get the last residue contained in the secondary structure.
     * @return The last residue contained in the secondary structure.
     */
    val lastResidue: Residue
        get() = residuesContained!![residuesContained!!.size - 1]

    /**
     * Define possible types of secondary structure.
     */
    enum class StructureType {
        betasheet, alphahelix
    }

    /**
     * Declare a new secondary structure.
     * @param secondaryStructureType The type of the secondary structure.
     */
    internal constructor(secondaryStructureType: StructureType) {
        this.secondaryStructureType = secondaryStructureType
        residuesContained = ArrayList()
    }

    /**
     * Declare a new secondary structure.
     *
     * @param residues               The residues part of the secondary structure.
     * @param secondaryStructureType The type of the secondary structure.
     */
    internal constructor(secondaryStructureType: StructureType, residues: ArrayList<Residue>) {
        this.secondaryStructureType = secondaryStructureType
        residuesContained = residues
    }

    /**
     * Add a residue to the secondary structure. Assumes that it has been checked if the residue
     * fits to this secondary structure.
     * @param residue The residue to be added.
     */
    internal fun addResidue(residue: Residue) {
        this.residuesContained.add(residue)
    }
}
