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
import itasserui.lib.filemanager.FileManager
import itasserui.lib.filemanager.WatchedDirectory
import itasserui.lib.store.Database
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.objects.ObjectFilter
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.conf.global
import org.kodein.di.generic.instance

class ProfileManager(
    override val kodein: Kodein = Kodein.global
) : Logger, KodeinAware {

    val fileManager: FileManager by instance()
    val database: Database by instance()

    fun createUserProfile(user: Account): Ior<Nel<RuntimeError>, Account> {
        val realUser = user.toUser()
        val mkdir = createProfileDir(realUser)
        val saveDb = trySaveToDb(realUser)
        return when {
            mkdir is Err && saveDb is Err -> Ior.Left(Nel(mkdir.a, listOf(saveDb.a)))
            mkdir is Err && saveDb is OK -> Both(Nel(mkdir.a), realUser)
            mkdir is OK  && saveDb is Err -> Both(Nel(saveDb.a), realUser)
            else -> Ior.Right(realUser)
        }
    }

    fun existsInFileSystem(user: Account) =
        fileManager.exists(user)

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

    private fun createProfileDir(user: Account): Outcome<WatchedDirectory> =
        if (!existsInFileSystem(user))
            setupUserDirectories(user)
        else UserDirectoryAlreadyExists(
            user,
            fileManager.fullPath(user)
        ).left()

    internal fun setupUserDirectories(user: Account) =
        fileManager
            .new(user)
            .also { fileManager[user, user.categories] }


    private fun anyUserExists(op: () -> ObjectFilter): Option<User> =
        database.find<User>(op)
            .toOption()
            .flatMap { it.firstOrNone() }

}