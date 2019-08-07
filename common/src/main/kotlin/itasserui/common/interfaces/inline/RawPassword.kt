package itasserui.common.interfaces.inline

import com.fasterxml.jackson.annotation.JsonValue
import itasserui.common.extensions.validPassword
import org.mindrot.jbcrypt.BCrypt

interface Password {
    val value: String
}

data class RawPassword(override val value: String): Password {
    val hashed get() = HashedPassword(BCrypt.hashpw(value, BCrypt.gensalt(10)))
    val isValid get() = value.validPassword()
}

data class HashedPassword(@JsonValue override val value: String): Password {
    fun verify(password: RawPassword) =
        BCrypt.checkpw(password.value, value)
}
