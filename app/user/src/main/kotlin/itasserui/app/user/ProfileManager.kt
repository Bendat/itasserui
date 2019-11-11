package itasserui.app.user

import arrow.core.*
import arrow.data.Ior
import arrow.data.Ior.Both
import arrow.data.Nel
import itasserui.app.user.ProfileError.*
import itasserui.app.user.User.UserCategory
import itasserui.app.user.User.UserCategory.*
import itasserui.common.`typealias`.Err
import itasserui.common.`typealias`.OK
import itasserui.common.`typealias`.Outcome
import itasserui.common.errors.RuntimeError
import itasserui.common.extensions.isFalse
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.logger.Logger
import itasserui.common.utils.uuid
import itasserui.lib.filemanager.FileManager
import itasserui.lib.filemanager.WatchedDirectory
import itasserui.lib.store.Database
import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.list.ObservableListWrapper
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.objects.ObjectFilter
import java.lang.System.currentTimeMillis
import java.time.Duration
import java.util.*

class ProfileManager(
    val fileManager: FileManager,
    val database: Database
) : Logger {
    @Suppress("MemberVisibilityCanBePrivate")
    val profiles: ObservableList<Profile> = ObservableListWrapper()
    val sessions: Map<UUID, Session?> = mutableMapOf()

    data class Session(val start: Long, val duration: Duration) {
        val sessionTimeRemaining: Long get() = start + duration.toMillis() - currentTimeMillis()
        @Suppress("MemberVisibilityCanBePrivate", "unused")
        val isLocked
            get() = sessionTimeRemaining < 0
        val isActive get() = !isLocked
    }

    fun isLoggedIn(profile: Profile) = isLoggedIn(profile.user)
    fun isLoggedIn(user: User) = isLoggedIn(user.id)
    fun isLoggedIn(user: UUID) = (sessions[user]?.isActive == true)
    fun isLoggedIn(user: Username): Boolean {
        return when (val profile = profiles.firstOrNull { it.user.username == user }) {
            null -> false
            else -> isLoggedIn(profile.user.id)
        }
    }

    fun find(id: UUID) = profiles.firstOrNull() { it.user.id == id }
    fun find(name: Username) = profiles.firstOrNull() { it.user.username == name }
    @Suppress("unused")
    fun getAdmin(password: RawPassword): Outcome<User> {
        val results = database.read<User> { Account::isAdmin eq true }
        lateinit var user: User
        return results
            .map { it.first() }
            .map { user = it; it }
            .flatMap { if (it.password.verify(password)) it.right() else WrongPassword(it).left() }
            .map { user }
    }

    fun perform(user: User, onNotLoggedIn: () -> Unit, op: () -> Unit) {
        if (!isLoggedIn(user))
            onNotLoggedIn()
        if (isLoggedIn(user))
            op()
    }

    fun perform(user: Username, onNotLoggedIn: () -> Unit, op: () -> Unit) {
        if (!isLoggedIn(user))
            onNotLoggedIn()
        if (isLoggedIn(user))
            op()
    }

    fun login(
        user: Account,
        password: RawPassword,
        duration: Duration = Duration.ZERO
    ): Outcome<Profile> {
        info { "Continuing login for ${duration.toMillis()}" }
        info { "Profiles are for ${profiles.toList()}" }
        return when (val users = database.read<User> { User::username eq user.username }) {
            is Err -> Either.Left(LoginFailedError(user, users.a))
            is OK -> when {
                users.b.isEmpty() -> Either.Left(NoSuchUser(user))
                users.b.first().checkPassword(password).isFalse -> Either.Left(WrongPassword(user))
                else -> getOrAddProfile(user.toUser(), Session(currentTimeMillis(), duration))
            }
        }
    }

    fun addSession(user: User, session: Session?) {
        sessions as MutableMap
        sessions[user.id] = session
    }

    private fun getOrAddProfile(user: User, session: Session? = null): Outcome<Profile> {
        return when (profiles.any { it.user.username == user.username }) {
            true -> profiles.first { it.user.username == user.username }
                .also { addSession(it.user, session) }
                .right()
            false -> getProfile(user, session)
        }
    }

    private fun getProfile(user: User, session: Session?): Either<RuntimeError, Profile> {
        return database.find<User> { User::username eq user.username }
            .flatMap { it.firstOrNone().toEither { NoSuchUser(user) } }
            .map {
                val directories = fileManager.getDirectories(user)
                val profile = profiles.find { p -> p.user.username == user.username } ?: Profile(
                    user = it,
                    directories = directories.toList().map { it.second },
                    dataDir = directories.getValue(DataDir),
                    outDir = directories.getValue(OutDir),
                    settings = directories.getValue(Settings),
                    manager = this
                )
                info { "Profiles are ${profiles.find { it.user.id == user.id }} " }
                profiles += profile
                addSession(profile.user, session)
                profile
            }
    }

    fun login(
        username: Username,
        password: RawPassword,
        duration: Duration
    ): Outcome<Profile> =
        login(UnregisteredUser(username, password, EmailAddress("")), password, duration)

    fun new(user: Account): Ior<Nel<RuntimeError>, User> {
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

    fun findUser(name: Username): Either<RuntimeError, Profile> {
        val user = User(uuid, name, RawPassword("").hashed, EmailAddress(""), false)
        return getProfile(user, null)
    }

    fun removeProfile(user: User): Option<User> {
        val result = profiles.removeIf { it.user.id == user.id }
        return when (result) {
            true -> Some(user)
            false -> None
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProfileManager

        if (database != other.database) return false

        return true
    }


    data class Profile(
        val user: User,
        val directories: List<WatchedDirectory>,
        val dataDir: WatchedDirectory,
        val outDir: WatchedDirectory,
        val settings: WatchedDirectory,
        private val manager: ProfileManager
    ) {
        fun login(user: User, password: RawPassword, duration: Duration) =
            manager.login(user, password, duration)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Profile

            if (user.id != other.user.id) return false

            return true
        }

        override fun hashCode(): Int {
            return user.id.hashCode()
        }

    }

}