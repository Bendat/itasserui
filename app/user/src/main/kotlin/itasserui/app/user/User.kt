@file:Suppress("unused")

package itasserui.app.user

import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.HashedPassword
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.serialization.DBObject
import itasserui.lib.filemanager.FileDomain
import org.mindrot.jbcrypt.BCrypt
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


data class User(
    override val id: UUID,
    override val username: Username,
    override val password: HashedPassword,
    override val emailAddress: EmailAddress,
    override val isAdmin: Boolean = false
) : Account, FileDomain, DBObject {
    override val category = FileDomain.FileCategory.Users
    override val relativeRootName get() = username.value
    override val categories = UserCategory.values().toList()

    fun checkPassword(password: RawPassword) =
        BCrypt.checkpw(password.value, this.password.value)

    override fun toUser(id: UUID): User = this

    enum class UserCategory : FileDomain.Subcategory {
        Settings,
        DataDir,
        OutDir;

        override val directory: Path
            get() = Paths.get(name.toLowerCase())
    }

}