package itasserui.common.extensions

import java.time.Duration

fun Duration.format(): String {
    val absSeconds = kotlin.math.abs(seconds)
    val positive = String.format(
        "%d:%02d:%02d",
        absSeconds / 3600,
        absSeconds % 3600 / 60,
        absSeconds % 60
    )
    return if (seconds < 0) "-$positive" else positive
}