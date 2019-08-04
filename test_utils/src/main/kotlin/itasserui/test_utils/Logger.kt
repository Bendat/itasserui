package itasserui.test_utils


import org.apache.commons.lang3.text.WordUtils
import org.slf4j.LoggerFactory
import java.lang.Thread.currentThread

typealias LogLevel = (String) -> Unit

internal interface Logger {
    val klog
        get() = LoggerFactory.getLogger(this::class.java)

    /*
    * Retrieves a stacktrace for this log, with a range
    * of potentially useful calls.
    */
    private val stackTrace
        get() = currentThread().stackTrace.mapIndexed { level, it ->
            "\t\t[$level][$it]\n"
        }.filterIndexed { level, _ ->
            (level in 3..9)
        }.joinToString(separator = "")

    fun info(op: Any?) = write(op, true) { klog.info(it) }
    fun trace(op: Any?) = write(op, true) { klog.trace(it) }
    fun debug(op: Any?) = write(op, true) { klog.debug(it) }
    fun error(op: Any?) = write(op, true) { klog.error(it) }
    fun warn(op: Any?) = write(op, true) { klog.warn(it) }

    fun info(format: Boolean = true, op: () -> Any?) =
        write(op(), format) { klog.info(it) }

    fun hook(format: Boolean = true, op: () -> Any?) =
        write(op(), format) { klog.trace("Shutdown hook executing: $it") }

    fun trace(format: Boolean = true, op: () -> Any?) =
        write(op(), format) { klog.trace(it) }

    fun debug(format: Boolean = true, op: () -> Any) =
        write(op(), format) { klog.debug(it) }

    fun error(format: Boolean = true, op: () -> Any?) =
        write(op(), format) { klog.error(it) }

    fun warn(format: Boolean = true, op: () -> Any?) =
        write(op(), format) { klog.warn(it) }

    private inline fun write(
        msg: Any?,
        format: Boolean = true,
        logType: LogLevel
    ) {

        val sb = if (format) WordUtils.wrap(
            msg.toString(), 180, "\n\t",
            false, " "
        )//.substring(0, min(3000, msg.toString().length))
        else msg

        logType(msg.toString())
    }
}