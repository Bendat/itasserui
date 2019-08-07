package itasserui.app.user

import arrow.core.*
import itasserui.app.user.ProfileError.UserEmailAlreadyExists
import itasserui.app.user.ProfileError.UsernameAlreadyExists
import itasserui.common.logger.Logger
import itasserui.lib.filemanager.FileDomain.FileCategory.Users
import itasserui.lib.filemanager.FileManager
import itasserui.lib.store.Database
import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.list.observableListOf
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.objects.ObjectFilter

class ProfileManager(
    val fileManager: FileManager,
    val database: Database,
    val users: ObservableList<User> = observableListOf()
) : Logger {

    fun existsInFileSystem(user: Account){
        fileManager[Users, user]
    }

    fun existsInDatabase(user: Account): Option<ProfileError> {
        return when {
            userExists(user) is Some -> UserExists(user).some()
            usernameExists(user) is Some -> UsernameAlreadyExists(user, usernameExists(user)).some()
            emailExists(user) is Some -> UserEmailAlreadyExists(user, emailExists(user)).some()
            else -> None
        }
    }

    internal fun usernameExists(user: Account): Option<User> =
        anyUserExists { User::username eq user.username }


    internal fun emailExists(user: Account): Option<User> =
        anyUserExists { User::emailAddress eq user.emailAddress }

    internal fun userExists(user: Account): Option<User> =
        usernameExists(user) and emailExists(user)

    private fun anyUserExists(op: () -> ObjectFilter): Option<User> =
        database.find<User>(op).toOption().flatMap { it.firstOrNone() }

}