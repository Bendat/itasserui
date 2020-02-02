package itasserui.app.viewer.pdb.parser

enum class AminoAcid(val symbol: String, val molecule: String) {
    ALA("A", "Alanine"), ARG("R", "Arginine"), ASNPair("N", "Asparagine"),
    ASP("D", "Aspartic Acid"), CYS("C", "Cysteine"), GLU("E", "Glutamic Acid"),
    GLN("Q", "Glutamine"), GLY("G", "Glycine"), HIS("H", "Histidine"),
    ILE("I", "Isoleucine"), LEU("L", "Leucine"), LYS("K", "Lysine"),
    MET("M", "Methionine"), PHE("F", "Phenylalanine"), PRO("P", "Proline"),
    SER("S", "Serine"), THR("T", "Threonine"), TRP("W", "Tryptophan"),
    TYR("Y", "Tyrosine"), VAL("V", "Valine")
}

data class Residue(
    val sequenceNo: Int, val acid: AminoAcid,
    val cAtom: Atom, val nAtom: Atom, val oAtom: Atom,
    val cAlphaAtom: Atom, val cBetaAtom: Atom
){
    val atoms get() = arrayOf(nAtom, cAtom, cAlphaAtom, cBetaAtom, oAtom)
}

