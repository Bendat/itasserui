package itasserui.common.utils

fun String.splitCamelcase(): String =
    splitCamelCase(this)


fun splitCamelCase(s: String): String {
    return s.replace(
        String.format(
            "%s|%s|%s",
            "(?<=[A-Z])(?=[A-Z][a-z])",
            "(?<=[^A-Z])(?=[A-Z])",
            "(?<=[A-Za-z])(?=[^A-Za-z])"
        ).toRegex(), " "
    )
}