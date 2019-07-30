package itasserui.common.utils

import java.lang.Thread.yield

fun until(max: Int = 8000, op: () -> Boolean) {
    val time = System.currentTimeMillis()
    while (!op()) {
        yield()
        val time2 = System.currentTimeMillis()
        if (time2 - time > max) break
    }
}