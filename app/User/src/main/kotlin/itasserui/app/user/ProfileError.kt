package itasserui.app.user

import arrow.core.Option
import itasserui.common.errors.PasswordValidationError
import itasserui.common.errors.RuntimeError

typealias BadWrite = ProfileError.DatabaseWriteError
typealias NoSuchUser = ProfileError.NoSuchUserError
typealias UserExists = ProfileError.UserAlreadyExists
typealias BadPassword = ProfileError.InvalidUserPasswordError
typealias CannotCreateDirectory = ProfileError.FileWriteError
typealias CannotDeleteUserDirectory = ProfileError.CannotDeleteUserDirectoryError
typealias CannotDeleteUserProfile = ProfileError.CannotDeleteUserDirectoryError
typealias WrongPassword = ProfileError.WrongPasswordError

sealed class ProfileError : RuntimeError() {
    class UserAlreadyExists(val user: UnregisteredUser) : ProfileError()
    class UsernameAlreadyExists(val user: UnregisteredUser, val match: Option<User>) : ProfileError()
    class UserEmailAlreadyExists(val user: UnregisteredUser, val match: Option<User>) : ProfileError()
    class NoSuchUserError(val user: User) : ProfileError()
    class DatabaseWriteError(val user: UnregisteredUser, val error: RuntimeError) : ProfileError()
    class CannotDeleteUserDirectoryError(val user: User, val error: RuntimeError) : ProfileError()
    class CannotDeleteUserProfileError(val user: User, val error: RuntimeError) : ProfileError()
    class FileWriteError(val user: User, val error: RuntimeError) : ProfileError()
    class WrongPasswordError(val user: User) : ProfileError()
    class InvalidUserPasswordError(
        val user: UnregisteredUser,
        val error: PasswordValidationError
    ) : ProfileError()
}