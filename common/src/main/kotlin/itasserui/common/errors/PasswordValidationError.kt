package itasserui.common.errors

typealias BadPassword = PasswordValidationError
typealias BadLength = PasswordValidationError.PasswordLengthError
typealias ShortPassword = PasswordValidationError.PasswordLengthError.ShortPasswordError
typealias LongPassword = PasswordValidationError.PasswordLengthError.LongPasswordError
typealias MissingCharset = PasswordValidationError.SpecialCharsMissingError
typealias MissingUpper = PasswordValidationError.MissingUppercaseChars
typealias MissingLower = PasswordValidationError.MissingLowercaseChars

sealed class PasswordValidationError(val message: String?) : RuntimeError() {
    sealed class PasswordLengthError(message: String) : PasswordValidationError(message) {
        abstract val length: Int
        abstract val threshold: Int

        class ShortPasswordError(
            override val length: Int,
            override val threshold: Int
        ) : PasswordLengthError("Too short [$length/$threshold]")

        class LongPasswordError(
            override val length: Int,
            override val threshold: Int
        ) : PasswordLengthError("Too long [$length/$threshold]")
    }

    class SpecialCharsMissingError(
        val charset: String
    ) : PasswordValidationError("Must contains one special character from $charset")

    class MissingUppercaseChars : PasswordValidationError("Requires at least one uppercase character")
    class MissingLowercaseChars : PasswordValidationError("Requires at least one lowercase character")
}