@file:Suppress("unused")

package itasserui.lib.fasta

import itasserui.common.logger.Logger
import itasserui.common.utils.uuid
import itasserui.lib.fasta.description.Description
import java.util.*


typealias SeqFile = List<Sequence>


data class Sequence(val description: Description, val body: SequenceChain)

sealed class SequenceChain {
    abstract val chain: String
    val id: UUID = uuid

    data class ValidSequenceChain(override val chain: String) : SequenceChain()

    data class InvalidSequenceChain(
        override val chain: String,
        val error: SequenceError.ParseError
    ) : SequenceChain()

    object EmptySequenceChain : SequenceChain() {
        override val chain: String = "==This Chain Has No Body=="
        override fun toString(): String {
            return "EmptySequenceChain(chain='$chain')"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SequenceChain

        if (chain != other.chain) return false

        return true
    }

    override fun hashCode(): Int {
        return chain.hashCode()
    }

}

enum class AminoAcid(val code: Char, val abbreviation: String, val aminoacid: String) {
    A('A', "ALA", "Alanine"),
    B('B', "ASX", "Asparagine"),
    C('C', "CYS", "Cystine"),
    D('D', "ASP", "Aspartate"),
    E('E', "GLU", "Glutamate"),
    F('F', "PHE", "Phenylalanine"),
    G('G', "GLY", "Glycine"),
    H('H', "HIS", "Histidine"),
    I('I', "ILE", "Isoleucine"),
    K('K', "LYS", "Lysine"),
    L('L', "LEU", "Leucine"),
    M('M', "MET", "Methionine"),
    N('N', "ASN", "Asparagine "),
    P('P', "PRO", "Proline"),
    Q('Q', "GLN", "Glutamine"),
    R('R', "ARG", "Arginine"),
    S('S', "SER", "Serine"),
    T('T', "THR", "Threonine"),
    V('V', "VAL", "Valine"),
    W('W', "TRP", "Tryptophan"),
    U('U', "SEL", "Selenocysteine"),
    Y('Y', "TYR", "Tyrosine"),
    Z('Z', "GLX", "Glutamine"),
    X('X', "", "Any"),
    Stop('*', "", "Translation stop"),
    Gap('-', "", "Gap of unknown length");

    companion object : Logger {
        fun fromAbbreviation(abbr: String) =
            values().first { it.abbreviation == abbr }


    }

}
