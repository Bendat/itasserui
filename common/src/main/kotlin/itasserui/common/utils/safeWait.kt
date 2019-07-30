package itasserui.common.utils

fun safeWait(millis: Long) {
    val time = System.currentTimeMillis()
    while (true) {
        if (System.currentTimeMillis() - time > millis)
            break
    }
}
