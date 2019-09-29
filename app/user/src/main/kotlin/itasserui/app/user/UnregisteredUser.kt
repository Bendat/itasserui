package itasserui.app.user

import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.Password
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.utils.uuid
import itasserui.lib.filemanager.FileDomain
import itasserui.lib.filemanager.FileDomain.FileCategory
import itasserui.lib.filemanager.FileDomain.FileCategory.Users
import itasserui.lib.filemanager.FileDomain.Subcategory
import java.util.*

interface Account : FileDomain {
    val username: Username
    val password: Password
    val emailAddress: EmailAddress
    fun toUser(id: UUID = this.id): User
}

data class UnregisteredUser(
    override val username: Username,
    override val password: RawPassword,
    override val emailAddress: EmailAddress
) : Account {
    override val category: FileCategory
        get() = Users
    override val relativeRootName: String
        get() = "tmp/${username.value}"
    override val id: UUID = uuid
    override val categories: List<Subcategory> = listOf()

    override fun toUser(id: UUID) = User(
        username = username,
        password = password.hashed,
        emailAddress = emailAddress,
        id = id
    )
}