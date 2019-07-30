package itasserui.common.errors

import itasserui.common.errors.RuntimeError

sealed class ParsingError : RuntimeError() {
    abstract val json: String

    class BadJsonError(override val json: String) : ParsingError()
}