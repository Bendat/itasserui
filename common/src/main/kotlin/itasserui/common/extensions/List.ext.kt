package itasserui.common.extensions

fun <T> List<T>.use(op: (T) -> Unit) =
    map {
        op(it)
        it
    }

fun <T, R> Collection<T>.loop(
    action: (T) -> Unit,
    transform: (T) -> R
): List<R> {
    return map { action(it); transform(it) }
}

fun <K, V> Map<K, V>.having(key: K, action: (V) -> Unit) {
    this[key]?.apply(action)
}