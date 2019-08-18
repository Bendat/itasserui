@file:Suppress("unused")

package itasserui.app.user

import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.utils.Fake
import itasserui.common.utils.uuid

object UserMocks {
    val user: User
        get() = User(
            username = Username(Fake.name().username()),
            password = RawPassword(Fake.internet().password(6, 8, true, true, true)).hashed,
            emailAddress = EmailAddress(Fake.internet().emailAddress()),
            id = uuid
        )


    val unregisteredUser: UnregisteredUser
        get() = UnregisteredUser(
            username = Username(Fake.name().username()),
            password = RawPassword(Fake.internet().password(6, 8, true, true, true)),
            emailAddress = EmailAddress(Fake.internet().emailAddress())
        )
}