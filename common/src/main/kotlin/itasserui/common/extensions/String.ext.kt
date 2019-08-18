package itasserui.common.extensions

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.Validated
import itasserui.common.errors.*


fun String.remove(vararg character: String): String {
    var orig = this
    character.forEach { orig = orig.replace(it, "") }
    return orig
}

private val spcharset = """!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~"""
private val specialChars = spcharset.toList()
fun String.validPassword(
    minLength: Int = 8,
    maxLength: Int = 1024,
    capitals: Boolean = true,
    specials: Boolean = true
): Validated<BadPassword, String> = when {
    length > maxLength ->
        Invalid(LongPassword(length, maxLength))
    length < minLength ->
        Invalid(ShortPassword(length, minLength))
    !any { it.isUpperCase() } && capitals ->
        Invalid(MissingUpper())
    !any { it.isLowerCase() } && capitals ->
        Invalid(MissingLower())
    !any { specialChars.contains(it) } && specials ->
        Invalid(MissingCharset(spcharset))
    else -> Valid(this)
}