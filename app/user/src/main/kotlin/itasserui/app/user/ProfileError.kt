@file:Suppress("unused")

package itasserui.app.user

import arrow.core.Option
import itasserui.common.errors.PasswordValidationError
import itasserui.common.errors.RuntimeError
import java.nio.file.Path

typealias BadWrite = ProfileError.DatabaseWriteError
typealias NoSuchUser = ProfileError.NoSuchUserError
typealias UserExists = ProfileError.UserAlreadyExists
typealias BadPassword = ProfileError.InvalidUserPasswordError
typealias CannotCreateDirectory = ProfileError.FileWriteError
typealias CannotDeleteUserDirectory = ProfileError.CannotDeleteUserDirectoryError
typealias CannotDeleteUserProfile = ProfileError.CannotDeleteUserDirectoryError
typealias WrongPassword = ProfileError.WrongPasswordError

sealed class ProfileError(parentError: RuntimeError? = null) : RuntimeError(parentError) {
    class UserAlreadyExists(val user: Account) : ProfileError()
    class CannotCreateUserProfileError(val user: Account, parent: RuntimeError) : ProfileError(parent)
    class UsernameAlreadyExists(val user: Account, val match: Option<Account>) : ProfileError()
    class UserDirectoryAlreadyExists(val user: Account, val path: Path) : ProfileError()
    class UserEmailAlreadyExists(val user: Account, val match: Option<Account>) : ProfileError()
    class NoSuchUserError(val user: Account) : ProfileError()
    class DatabaseWriteError(val user: Account, val error: RuntimeError) : ProfileError()
    class CannotDeleteUserDirectoryError(val user: Account, val error: RuntimeError) : ProfileError()
    class CannotDeleteUserProfileError(val user: Account, val error: RuntimeError) : ProfileError()
    class FileWriteError(val user: Account, val error: RuntimeError) : ProfileError()
    class WrongPasswordError(val user: Account) : ProfileError()
    class InvalidUserPasswordError(
        val user: Account,
        val error: PasswordValidationError
    ) : ProfileError()


}