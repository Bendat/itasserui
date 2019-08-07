package itasserui.app.user

import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.HashedPassword
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.serialization.DBObject
import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.list.observableListOf
import org.mindrot.jbcrypt.BCrypt
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import itasserui.lib.filemanager.FileDomain
import itasserui.lib.filemanager.WatchedDirectory


data class User(
    override val id: UUID,
    override val username: Username,
    override val password: HashedPassword,
    override val emailAddress: EmailAddress
) : FileDomain, DBObject, Account {
    override var directories: ObservableList<WatchedDirectory> = observableListOf()
    override val category = FileDomain.FileCategory.Users
    override val directoryName get() = username.value

    fun checkPassword(password: RawPassword) =
        BCrypt.checkpw(password.value, this.password.value)

    enum class UserCategories(override val directory: Path) : FileDomain.Subcategory {
        Settings(Paths.get("settings")),
        DataDir(Paths.get("datadir")),
        OutDir(Paths.get("outdir")),
    }
}