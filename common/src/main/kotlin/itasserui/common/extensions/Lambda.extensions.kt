package itasserui.common.extensions

infix fun <T> (() -> T).unless(condition: Boolean): Boolean {
    return if (!condition) {
        this()
        true
    } else false
}



