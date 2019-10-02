package itasserui.app.user

import arrow.core.*
import arrow.data.Ior
import arrow.data.Ior.Both
import arrow.data.Nel
import itasserui.app.user.ProfileError.*
import itasserui.app.user.User.UserCategory
import itasserui.common.`typealias`.Err
import itasserui.common.`typealias`.OK
import itasserui.common.`typealias`.Outcome
import itasserui.common.errors.RuntimeError
import itasserui.common.extensions.isFalse
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.logger.Logger
import itasserui.lib.filemanager.FileManager
import itasserui.lib.filemanager.WatchedDirectory
import itasserui.lib.store.Database
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.objects.ObjectFilter
import java.lang.System.currentTimeMillis
import java.time.Duration

class ProfileManager(
    val fileManager: FileManager,
    val database: Database
) : Logger {

    data class TimeLock(val start: Long, val duration: Duration) {
        val timeRemaining get() = start + duration.toMillis() - currentTimeMillis()
        val isLocked get() = timeRemaining < 0
        val isUnlocked get() = !isLocked
    }

    data class Session(val user: User, val timeLock: TimeLock) {
        val isActive get() = timeLock.isUnlocked
        val sessionTimeRemaining: Long get() = timeLock.timeRemaining
    }

    fun getAdmin(password: RawPassword): Outcome<User> {
        val results = database.read<User> { Account::isAdmin eq true }
        lateinit var user: User
        return results
            .map { it.first() }
            .map { user = it; it }
            .flatMap { if (it.password.verify(password)) it.right() else WrongPassword(it).left() }
            .map { user }
    }

    fun login(
        user: Account,
        password: RawPassword,
        duration: Duration = Duration.ZERO
    ): Outcome<Session> {
        return when (val users = database.read<User> { User::username eq user.username }) {
            is Err -> Either.Left(LoginFailedError(user, users.a))
            is OK -> when {
                users.b.isEmpty() -> Either.Left(NoSuchUser(user))
                users.b.first().checkPassword(password).isFalse -> Either.Left(WrongPassword(user))
                else -> Either.Right(Session(users.b.first(), TimeLock(currentTimeMillis(), duration)))
            }
        }
    }

    fun login(
        username: Username,
        password: RawPassword,
        duration: Duration
    ): Outcome<Session> =
        login(UnregisteredUser(username, password, EmailAddress("")), password, duration)

    fun createUserProfile(user: Account): Ior<Nel<RuntimeError>, User> {
        val realUser = user.toUser()
        val mkdir = createProfileDir(realUser)
        val saveDb = trySaveToDb(realUser)
        return when {
            mkdir is Err && saveDb is Err -> Ior.Left(Nel(mkdir.a, listOf(saveDb.a)))
            mkdir is Err && saveDb is OK -> Both(Nel(mkdir.a), realUser)
            mkdir is OK && saveDb is Err -> Both(Nel(saveDb.a), realUser)
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
        database
            .create(user)
            .map { user }

    fun getUserDir(user: User, category: UserCategory) =
        fileManager[user].map { it.path.resolve(category.directory) }

    private fun trySaveToDb(user: User): Outcome<User> = when (val exists = existsInDatabase(user)) {
        is None -> saveToDb(user)
        is Some -> CannotCreateUserProfileError(user, exists.t).left()
    }

    private fun createProfileDir(user: Account): Outcome<WatchedDirectory> = when {
        !existsInFileSystem(user) -> setupUserDirectories(user)
        else -> Left(UserDirectoryAlreadyExists(user, fileManager.fullPath(user)))
    }

    internal fun setupUserDirectories(user: Account) =
        fileManager
            .new(user)
            .also { fileManager[user, user.categories] }


    private fun anyUserExists(op: () -> ObjectFilter): Option<User> =
        database
            .find<User>(op)
            .toOption()
            .flatMap { it.firstOrNone() }

}