package itasserui.app.user

import itasserui.common.interfaces.Identifiable
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.Password
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.utils.uuid
import java.util.*

interface Account : Identifiable {
    val username: Username
    val password: Password
    val emailAddress: EmailAddress
}

data class UnregisteredUser(
    override val username: Username,
    override val password: RawPassword,
    override val emailAddress: EmailAddress
) : Account {
    override val id: UUID = uuid
}