package itasserui.common.errors

typealias BadPassword = PasswordValidationError
typealias BadLength = PasswordValidationError.PasswordLengthError
typealias ShortPassword = PasswordValidationError.PasswordLengthError.ShortPasswordError
typealias LongPassword = PasswordValidationError.PasswordLengthError.LongPasswordError
typealias MissingCharset = PasswordValidationError.SpecialCharsMissingError
typealias MissingUpper = PasswordValidationError.MissingUppercaseChars
typealias MissingLower = PasswordValidationError.MissingLowercaseChars

sealed class PasswordValidationError : RuntimeError() {
    sealed class PasswordLengthError : PasswordValidationError() {
        abstract val length: Int
        abstract val threshold: Int

        class ShortPasswordError(
            override val length: Int,
            override val threshold: Int
        ) : PasswordLengthError()

        class LongPasswordError(
            override val length: Int,
            override val threshold: Int
        ) : PasswordLengthError()
    }

    class SpecialCharsMissingError(
        val charset: String
    ) : PasswordValidationError()

    class MissingUppercaseChars(val message: String) : PasswordValidationError()
    class MissingLowercaseChars(val message: String) : PasswordValidationError()
}