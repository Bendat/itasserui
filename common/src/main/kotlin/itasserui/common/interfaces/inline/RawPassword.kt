package itasserui.common.interfaces.inline

import com.fasterxml.jackson.annotation.JsonValue
import itasserui.common.extensions.validPassword
import org.mindrot.jbcrypt.BCrypt

data class RawPassword(val value: String) {
    val hashed get() = RawPassword(BCrypt.hashpw(value, BCrypt.gensalt(10)))
    val isValid get() = value.validPassword()
}

data class HashedPassword(@JsonValue val value: String) {
    fun verify(password: RawPassword) =
        BCrypt.checkpw(password.value, value)
}
