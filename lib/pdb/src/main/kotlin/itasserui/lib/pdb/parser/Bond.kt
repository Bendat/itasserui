package itasserui.lib.pdb.parser

data class Bond(val from: Atomic, val to: Atomic, val text: String = "[No Text Provided]"){
    val isCAlphaBeta get() = from.element == Element.CA && to.element == Element.CB
}