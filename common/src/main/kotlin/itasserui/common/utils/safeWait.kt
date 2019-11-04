package itasserui.common.utils

import com.github.javafaker.Bool

fun safeWait(millis: Long) {
    val time = System.currentTimeMillis()
    while (true) {
        if (System.currentTimeMillis() - time > millis)
            break
    }
}

 fun safeWait(millis: Long, breakOn: () -> Boolean) {
    val time = System.currentTimeMillis()
    while (true) {
        if (System.currentTimeMillis() - time > millis)
            break
        if (breakOn())
            break
    }
}
