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

data class Residue(
    val sequenceNo: Int, val acid: AminoAcid,
    val cAtom: Atomic, val nAtom: Atomic, val oAtom: Atomic,
    val cAlphaAtom: Atomic, val cBetaAtom: Atomic
) {
    val atoms get() = arrayOf(nAtom, cAtom, cAlphaAtom, cBetaAtom, oAtom)
}

