@file:Suppress("unused")

package itasserui.app.user

import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.HashedPassword
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.serialization.DBObject
import itasserui.lib.filemanager.FileDomain
import org.mindrot.jbcrypt.BCrypt
import java.util.*


data class User(
    override val id: UUID,
    override val username: Username,
    override val password: HashedPassword,
    override val emailAddress: EmailAddress
) : Account, FileDomain, DBObject {
    override val category = FileDomain.FileCategory.Users
    override val relativeRootName get() = username.value
    override val categories = UserCategories.values().toList()

    fun checkPassword(password: RawPassword) =
        BCrypt.checkpw(password.value, this.password.value)

    override fun toUser(id: UUID): User = this

    enum class UserCategories : FileDomain.Subcategory {
        Settings,
        DataDir,
        OutDir,
    }

}