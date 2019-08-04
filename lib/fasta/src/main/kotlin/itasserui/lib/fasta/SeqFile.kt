@file:Suppress("unused")

package itasserui.lib.fasta

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

enum class AminoAcids(val code: Char, val abbreviation: String, val aminoacid: String) {
    A('A', "ALA", "alanine"),
    B('B', "ASX", "asparagine"),
    C('C', "CYS", "cystine"),
    D('D', "ASP", "aspartate"),
    E('E', "GLU", "glutamate  "),
    F('F', "PHE", "phenylalanine"),
    G('G', "GLY", "glycine"),
    H('H', "HIS", "histidine"),
    I('I', "ILE", "isoleucine"),
    K('K', "LYS", "lysine"),
    L('L', "LEU", "leucine"),
    M('M', "MET", "methionine"),
    N('N', "ASN", "asparagine "),
    P('P', "PRO", "proline"),
    Q('Q', "GLN", "glutamine"),
    R('R', "ARG", "arginine"),
    S('S', "SER", "serine"),
    T('T', "THR", "threonine"),
    V('V', "VAl", "valine"),
    W('W', "TRP", "tryptophan"),
    U('U', "", "selenocysteine"),
    Y('Y', "TYR", "tyrosine"),
    Z('Z', "GLX", "glutamine"),
    X('X', "", "Any"),
    Stop('*', "", "Translation stop"),
    Gap('-', "", "Gap of unknown length")
}
