package itasserui.app.viewer.pdbmodel

import javafx.util.Pair

/**
 * Representation of a residue, holding all relevant atoms of a particular residue.
 */
class Residue(var resNum: String, aminoAcid: String) {

    /**
     * C atom of the residue.
     */
    var cAtom: Atom? = null
    /**
     * O atom of the residue.
     */
    var oAtom: Atom? = null
    /**
     * N atom of the residue.
     */
    var nAtom: Atom? = null
    /**
     * C alpha atom of the residue.
     */
    var cAlphaAtom: Atom? = null
    /**
     * C beta atom of the residue.
     */
    var cBetaAtom: Atom? = null

    /**
     * If the residue is part of a secondary structure it is referenced here, else null.
     */
    var secondaryStructure: SecondaryStructure? = null

    /**
     * The current residues
     */
    /**
     * The amino acid this residue represents
     *
     * @return The amino acid this residue represents.
     */
    var aminoAcid: AminoAcid = AminoAcid.valueOf(aminoAcid)

    /**
     * Return the one letter code for each residue.
     *
     * @return One letter code of the amino acid.
     */
    val oneLetterAminoAcidName: String
        get() = aminoAcidMap[this.aminoAcid]?.key ?: "No key found"

    /**
     * Get the human readable name of the amino acid which this residue represents.
     *
     * @return Human readable name of an amino acid residue.
     */
    val name: String
        get() = aminoAcidMap[this.aminoAcid]?.value
            ?: "no name found"

    /**
     * Get a one letter type of this SecondaryStructure instance. H for Helix, E for beta sheet.
     *
     * @return H for helix, E for beta sheet.
     */
    // Not part of a secondary structue.
    val oneLetterSecondaryStructureType: String
        get() = secondaryStructure?.oneLetterSecondaryStructureType ?: ""


    /**
     * Get all atoms belonging to this residue as a list.
     *
     * @return List of all atoms.
     */
    internal val atoms: List<Atom>
        get() = listOfNotNull(nAtom, cAtom, cAlphaAtom, cBetaAtom, oAtom)

    enum class AminoAcid {
        ALA, ARG, ASN, ASP, CYS, GLU, GLN, GLY, HIS,
        ILE, LEU, LYS, MET, PHE, PRO, SER, THR, TRP,
        TYR, VAL
    }

    init {
        this.aminoAcid = AminoAcid.valueOf(aminoAcid)
        this.secondaryStructure = null
    }

    fun setAminoAcid(aminoAcid: String) {
        this.aminoAcid = AminoAcid.valueOf(aminoAcid)
    }

    /**
     * Return the one letter code for each residue.
     *
     * @return
     */
    override fun toString(): String {
        return resNum
    }

    companion object {

        private var aminoAcidMap: MutableMap<AminoAcid, Pair<String, String>> = mutableMapOf()

        init {
            aminoAcidMap[AminoAcid.ALA] = Pair("A", "Alanine")
            aminoAcidMap[AminoAcid.ARG] = Pair("R", "Arginine")
            aminoAcidMap[AminoAcid.ASN] = Pair("N", "Asparagine")
            aminoAcidMap[AminoAcid.ASP] = Pair("D", "Aspartic Acid")
            aminoAcidMap[AminoAcid.CYS] = Pair("C", "Cysteine")
            aminoAcidMap[AminoAcid.GLU] = Pair("E", "Glutamic Acid")
            aminoAcidMap[AminoAcid.GLN] = Pair("Q", "Glutamine")
            aminoAcidMap[AminoAcid.GLY] = Pair("G", "Glycine")
            aminoAcidMap[AminoAcid.HIS] = Pair("H", "Histidine")
            aminoAcidMap[AminoAcid.ILE] = Pair("I", "Isoleucine")
            aminoAcidMap[AminoAcid.LEU] = Pair("L", "Leucine")
            aminoAcidMap[AminoAcid.LYS] = Pair("K", "Lysine")
            aminoAcidMap[AminoAcid.MET] = Pair("M", "Methionine")
            aminoAcidMap[AminoAcid.PHE] = Pair("F", "Phenylalanine")
            aminoAcidMap[AminoAcid.PRO] = Pair("P", "Proline")
            aminoAcidMap[AminoAcid.SER] = Pair("S", "Serine")
            aminoAcidMap[AminoAcid.THR] = Pair("T", "Threonine")
            aminoAcidMap[AminoAcid.TRP] = Pair("W", "Tryptophan")
            aminoAcidMap[AminoAcid.TYR] = Pair("Y", "Tyrosine")
            aminoAcidMap[AminoAcid.VAL] = Pair("V", "Valine")
        }

        /**
         * Return the one letter code for each residue.
         *
         * @return One letter code of the amino acid.
         */
        fun getOneLetterAminoAcidName(aminoAcid: AminoAcid): String {
            return aminoAcidMap[aminoAcid]?.key
                ?: throw IllegalStateException("No item $aminoAcid in map $aminoAcidMap")
        }

        /**
         * Get the human readable name of the amino acid which this residue represents.
         *
         * @return Human readable name of an amino acid residue.
         */
        fun getName(aminoAcid: AminoAcid): String {
            return aminoAcidMap[aminoAcid]?.value
                ?: throw IllegalStateException("No item $aminoAcid in map $aminoAcidMap")
        }
    }
}
