package itasserui.app.user

import io.mockk.every
import io.mockk.mockk
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.utils.Fake
import itasserui.common.utils.uuid

object UserMocks {
    val user
        get() = mockk<User> {
            every { username } returns Username(Fake.name().username())
            every { password } returns RawPassword(Fake.internet().password(6, 8, true, true, true)).hashed
            every { emailAddress } returns EmailAddress(Fake.internet().emailAddress())
            every { id } returns uuid
        }
}