package itasserui.lib.process

import itasserui.common.errors.RuntimeError
import java.util.*

typealias Timeout = ProcessError.ProcessExceptionError.TimeoutError
typealias TerminationError = ProcessError.ProcessExceptionError.TerminationError
typealias FailedStart = ProcessError.ProcessExceptionError.StartError
typealias NoProcess = ProcessError.NoProcessError

sealed class ProcessError : RuntimeError() {
    abstract val processName: String

    sealed class ProcessExceptionError : ProcessError() {
        abstract val e: Throwable

        class StartError(
            override val processName: String,
            override val e: Throwable
        ) : ProcessExceptionError()

        class TimeoutError(
            override val processName: String,
            override val e: Throwable
        ) : ProcessExceptionError()

        class TerminationError(
            override val processName: String,
            override val e: Throwable
        ) : ProcessExceptionError()
    }

    class NoProcessError(
        override val processName: String
    ) : ProcessError()

    class ProcessCompletedError(
        override val processName: String,
        val id: UUID
    ) : ProcessError()
}