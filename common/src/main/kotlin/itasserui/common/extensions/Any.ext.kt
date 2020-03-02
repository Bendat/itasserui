package itasserui.common.extensions

import itasserui.common.logger.Logger

fun <T> T.print(): T {
    object : Logger {}.info { "Printing object value [$this]" }
    return this
}

val Any?.isNull
    get() = this == null

val Any?.notNull
    get() = this != null

data class LetPair<T, K>(val t: T?, val k: K?) {
    operator fun invoke(op: (T, K) -> Unit) {
        t?.let { t ->
            k?.let { k ->
                op(t, k)
            }
        }
    }
}

infix fun <T, K> T?.compose(other: K?) = LetPair(this, other)