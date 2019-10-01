package itasserui.common.extensions

inline fun Int.forEach(op: (Int) -> Unit) {
    (0..this).forEach {
        op(it)
    }
}

fun <T> Int.map(op: (Int) -> T): List<T> {
    val list = arrayListOf<T>()
    (0 until this).forEach {
        list += op(it)
    }
    return list
}