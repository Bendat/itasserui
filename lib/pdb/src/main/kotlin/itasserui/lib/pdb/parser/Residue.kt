package itasserui.lib.pdb.parser

enum class AminoAcid(val symbol: String, val molecule: String) {
    ALA("A", "Alanine"), ARG("R", "Arginine"), ASN("N", "Asparagine"),
    ASP("D", "Aspartic Acid"), CYS("C", "Cysteine"), GLU("E", "Glutamic Acid"),
    GLN("Q", "Glutamine"), GLY("G", "Glycine"), HIS("H", "Histidine"),
    ILE("I", "Isoleucine"), LEU("L", "Leucine"), LYS("K", "Lysine"),
    MET("M", "Methionine"), PHE("F", "Phenylalanine"), PRO("P", "Proline"),
    SER("S", "Serine"), THR("T", "Threonine"), TRP("W", "Tryptophan"),
    TYR("Y", "Tyrosine"), VAL("V", "Valine"),
    NUL("0", "Nothing")
}

interface Peptide {
    val sequenceNo: String
    val acid: AminoAcid
    val cAtom: Atomic
    val nAtom: Atomic
    val oAtom: Atomic
    val cAlphaAtom: Atomic
    val cBetaAtom: Atomic

    val atoms get() = arrayOf(nAtom, cAtom, cAlphaAtom, cBetaAtom, oAtom)
}

data class Residue(
    override val sequenceNo: String,
    override val acid: AminoAcid,
    override val cAtom: Atomic,
    override val nAtom: Atomic,
    override val oAtom: Atomic,
    override val cAlphaAtom: Atomic,
    override val cBetaAtom: Atomic
) : Peptide

object ResidueStub : Peptide {
    override val sequenceNo: String = "-1"
    override val acid: AminoAcid = AminoAcid.NUL
    override val cAtom: Atomic = EmptyAtom
    override val nAtom: Atomic = EmptyAtom
    override val oAtom: Atomic = EmptyAtom
    override val cAlphaAtom: Atomic = EmptyAtom
    override val cBetaAtom: Atomic = EmptyAtom
}
