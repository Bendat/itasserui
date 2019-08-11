package itasserui.app.user

import arrow.core.*
import arrow.data.Ior
import arrow.data.Ior.Both
import arrow.data.Nel
import itasserui.app.user.ProfileError.*
import itasserui.common.`typealias`.Err
import itasserui.common.`typealias`.OK
import itasserui.common.`typealias`.Outcome
import itasserui.common.errors.RuntimeError
import itasserui.common.logger.Logger
import itasserui.lib.filemanager.FileDomain.FileCategory
import itasserui.lib.filemanager.FileDomain.FileCategory.Users
import itasserui.lib.filemanager.FileManager
import itasserui.lib.filemanager.WatchedDirectory
import itasserui.lib.store.Database
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.objects.ObjectFilter

class ProfileManager(
    val fileManager: FileManager,
    val database: Database
) : Logger {

    fun createUserProfile(
        user: Account,
        fileCategory: FileCategory = Users
    ): Ior<Nel<RuntimeError>, Account> {
        val realUser = user.toUser()
        val mkdir = createProfileDir(realUser, fileCategory)
        val mkdb = trySaveToDb(realUser)
        return when {
            mkdir is Err && mkdb is Err -> Ior.Left(Nel(mkdir.a, listOf(mkdb.a)))
            mkdir is Err && mkdb is OK -> Both(Nel(mkdir.a), realUser)
            mkdir is OK && mkdb is Err -> Both(Nel(mkdb.a), realUser)
            else -> Ior.Right(realUser)
        }
    }

    fun existsInFileSystem(
        user: Account,
        fileCategory: FileCategory = Users
    ) = fileManager.exists(fileCategory, user)

    fun existsInDatabase(user: Account): Option<ProfileError> = when {
        userExists(user) is Some ->
            UserExists(user).some()
        usernameExists(user) is Some ->
            UsernameAlreadyExists(user, usernameExists(user)).some()
        emailExists(user) is Some ->
            UserEmailAlreadyExists(user, emailExists(user)).some()
        else -> None
    }

    fun usernameExists(user: Account): Option<User> =
        anyUserExists { User::username eq user.username }

    fun emailExists(user: Account): Option<User> =
        anyUserExists { User::emailAddress eq user.emailAddress }

    fun userExists(user: Account): Option<User> =
        usernameExists(user) and emailExists(user)

    private fun saveToDb(user: User): Outcome<User> =
        database.create(user).map { user }

    private fun trySaveToDb(user: User): Outcome<User> =
        when (val exists = existsInDatabase(user)) {
            is None -> saveToDb(user)
            is Some -> CannotCreateUserProfileError(user, exists.t).left()
        }

    private fun createProfileDir(
        user: Account,
        fileCategory: FileCategory = Users
    ): Outcome<WatchedDirectory> =
        if (!existsInFileSystem(user, fileCategory))
            fileManager.new(fileCategory, user)
        else UserDirectoryAlreadyExists(
            user,
            fileManager.fullPath(fileCategory, user)
        ).left()

    private fun anyUserExists(op: () -> ObjectFilter): Option<User> =
        database.find<User>(op).toOption().flatMap { it.firstOrNone() }

}