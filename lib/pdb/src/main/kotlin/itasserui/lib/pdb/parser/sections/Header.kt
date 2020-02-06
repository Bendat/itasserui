package itasserui.lib.pdb.parser.sections

sealed class Header {
    abstract val title: String
    abstract val code: String
}

object EmptyHeader : Header() {
    override val title: String = "No Title Provided"
    override val code: String = "N0HDR"
}

data class ValidHeader(
    override val title: String,
    override val code: String
) : Header()
