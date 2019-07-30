package itasserui.common.extensions

fun <T> List<T>.use(op: (T) -> Unit) =
    map {
        op(it)
        it
    }