package itasserui.common.extensions

import itasserui.common.logger.Logger

fun <T> T.print(): T {
    object : Logger {}.info { "Printing object value [$this]" }
    return this
}