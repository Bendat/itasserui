@file:Suppress("unused")

package itasserui.app.user

import arrow.core.Option
import itasserui.common.errors.PasswordValidationError
import itasserui.common.errors.RuntimeError
import itasserui.common.utils.splitCamelCase
import java.nio.file.Path

typealias BadWrite = ProfileError.DatabaseWriteError
typealias NoSuchUser = ProfileError.NoSuchUserError
typealias UserExists = ProfileError.UserAlreadyExists
typealias BadPassword = ProfileError.InvalidUserPasswordError
typealias CannotCreateDirectory = ProfileError.FileWriteError
typealias CannotDeleteUserDirectory = ProfileError.CannotDeleteUserDirectoryError
typealias CannotDeleteUserProfile = ProfileError.CannotDeleteUserDirectoryError
typealias WrongPassword = ProfileError.WrongPasswordError

sealed class ProfileError(val user: Account, parentError: RuntimeError? = null) : RuntimeError(parentError) {
    class NoSuchUserError(user: Account, parent: RuntimeError? = null) :
        ProfileError(user, parent)

    class DatabaseWriteError(user: Account, parent: RuntimeError? = null) :
        ProfileError(user, parent)

    class CannotDeleteUserDirectoryError(user: Account, parent: RuntimeError? = null) :
        ProfileError(user, parent)

    class CannotDeleteUserProfileError(user: Account, parent: RuntimeError? = null) :
        ProfileError(user, parent)

    class FileWriteError(user: Account, parent: RuntimeError? = null) :
        ProfileError(user, parent)

    class WrongPasswordError(user: Account, parent: RuntimeError? = null) :
        ProfileError(user, parent)

    class LoginFailedError(user: Account, parent: RuntimeError? = null) :
        ProfileError(user, parent)

    class UserAlreadyExists(user: Account, parent: RuntimeError? = null) :
        ProfileError(user, parent)

    class CannotCreateUserProfileError(user: Account, parent: RuntimeError? = null) :
        ProfileError(user, parent)

    class UsernameAlreadyExists(user: Account, val match: Option<Account>, parent: RuntimeError? = null) :
        ProfileError(user, parent)

    class UserDirectoryAlreadyExists(user: Account, val path: Path, parent: RuntimeError? = null) :
        ProfileError(user, parent)

    class UserEmailAlreadyExists(user: Account, val match: Option<Account>, parent: RuntimeError? = null) :
        ProfileError(user, parent)


    class InvalidUserPasswordError(
        user: Account,
        parent: PasswordValidationError
    ) : ProfileError(user, parent)

    val name get() = this::class.java.simpleName
    override fun toString(): String {
        val parError: String = if (parentError != null)
            parentError::class.java.simpleName
        else "No Parent Error Defined."
        return """ ${splitCamelCase(name.removeSuffix("Error"))}
            User: ${user.username}
            Parent Error: ${splitCamelCase(parError)}
        """.trimIndent()
    }

}
