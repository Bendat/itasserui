package itasserui.app.user

import itasserui.app.user.User.UserCategories.*
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.HashedPassword
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.serialization.DBObject
import itasserui.lib.filemanager.FileDomain
import itasserui.lib.filemanager.FileSystem
import itasserui.lib.filemanager.WatchedDirectory
import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.list.observableListOf
import org.mindrot.jbcrypt.BCrypt
import java.nio.file.Path
import java.util.*


data class User(
    override val id: UUID,
    override val username: Username,
    override val password: HashedPassword,
    override val emailAddress: EmailAddress
) : Account, FileDomain, DBObject {
    override var directories: ObservableList<WatchedDirectory> = observableListOf()
    override val category = FileDomain.FileCategory.Users
    override val relativeRootName get() = username.value
    val categories = Categories()

    fun checkPassword(password: RawPassword) =
        BCrypt.checkpw(password.value, this.password.value)

    override fun toUser(id: UUID): User = this

    enum class UserCategories(override val directory: Path) : FileDomain.Subcategory {
        Settings(FileSystem["settings"]),
        DataDir(FileSystem["datadir"]),
        OutDir(FileSystem["outdir"]),
    }

    inner class Categories {
        val settings = Settings
        val dataDir = DataDir
        val outDir = OutDir
    }
}