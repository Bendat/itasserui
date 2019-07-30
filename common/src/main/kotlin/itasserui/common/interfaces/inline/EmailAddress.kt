package itasserui.common.interfaces.inline

import com.fasterxml.jackson.annotation.JsonValue
import org.apache.commons.validator.routines.EmailValidator

data class EmailAddress(@JsonValue val value: String) {
    val isValid get() = EmailValidator.getInstance().isValid(value)
}
