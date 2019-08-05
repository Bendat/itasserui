package itasserui.common.errors

sealed class ParsingError : RuntimeError() {
    abstract val json: String

    class BadJsonError(override val json: String) : ParsingError()
}