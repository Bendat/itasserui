package itasserui.app.mytasser.lib

import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import javafx.util.StringConverter

val usernameStringConverter = object : StringConverter<Username>() {
    override fun toString(`object`: Username?): String =
        `object`?.value ?: ""

    override fun fromString(string: String?): Username =
        Username(string ?: "")
}

val passwordStringConverter = object : StringConverter<RawPassword>() {
    override fun toString(`object`: RawPassword?): String =
        `object`?.value ?: ""

    override fun fromString(string: String?): RawPassword =
        RawPassword(string ?: "")
}

