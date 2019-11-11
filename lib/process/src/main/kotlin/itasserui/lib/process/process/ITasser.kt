@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package itasserui.lib.process.process

import arrow.core.*
import com.fasterxml.jackson.annotation.JsonIgnore
import itasserui.common.`typealias`.Outcome
import itasserui.common.logger.Logger
import itasserui.common.serialization.JsonObject
import itasserui.common.utils.safeWait
import itasserui.lib.process.*
import itasserui.lib.process.details.ExecutionState
import itasserui.lib.process.details.ExecutionState.*
import itasserui.lib.process.details.ExitCode
import itasserui.lib.process.details.ProcessOutput
import itasserui.lib.process.details.TrackingList
import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.property.StandardObservableProperty
import org.joda.time.DateTime
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.ProcessResult
import org.zeroturnaround.exec.StartedProcess
import org.zeroturnaround.exec.stream.LogOutputStream
import org.zeroturnaround.process.ProcessUtil
import org.zeroturnaround.process.Processes.newStandardProcess
import org.zeroturnaround.process.SystemProcess
import java.lang.System.currentTimeMillis
import java.time.Duration
import java.util.*
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer
import kotlin.math.abs
import kotlin.math.max


class ITasser(
    val process: CCRCProcess,
    @JsonIgnore
    val listener: ITasserListener = ITasserListener(),
    state: ExecutionState = Queued,
    priority: Int = 0
) : JsonObject, Logger {
    @JsonIgnore
    val errors = TrackingList<ProcessError>()
    @JsonIgnore
    val stateProperty = StandardObservableProperty(state)
    var state: ExecutionState by stateProperty
    @JsonIgnore
    val priorityProperty = StandardObservableProperty(priority)
    var priority: Int by priorityProperty
    val std = STD()
    @JsonIgnore
    internal val executor = ExecutionContext()
    @JsonIgnore
    val startedTimeProperty = StandardObservableProperty(0L)
    var startedTime by startedTimeProperty

    private var timer: Option<Timer> = None
    private val startTimesPrivate = arrayListOf<Long>()
    val startTimes: List<Long> get() = startTimesPrivate
    private val endTimesPrivate = arrayListOf<Long>()
    val endTimes: List<Long> get() = endTimesPrivate

    @JsonIgnore
    val executionTimeProperty = StandardObservableProperty(0L)
    private var executionTimePrivate by executionTimeProperty
    val executionTime get() = executionTimePrivate

    inner class ExecutionContext {
        var future: Option<Future<ProcessResult>> = None
        private var realProcess: Option<StartedProcess> = None
        private var sysProcess: Option<SystemProcess> = None
        private val processExecutor: ProcessExecutor = ExecutorFactory.executor(process.args, listener)
        var exitCode: Option<ExitCode> = None
        val command get() = processExecutor.command.joinToString(" ")
        fun waitForFinish(): Option<ProcessResult> {
            return future.map {
                while (!it.isCancelled and !it.isDone) {
                    Thread.sleep(100)
                }
                it.get()
            }
        }

        fun waitForFinish(timeout: Duration): Option<ProcessResult> {
            return when (val f = future) {
                is None -> None
                is Some -> Try { f.t.get(timeout.toMillis(), TimeUnit.MILLISECONDS) }
                    .toOption()
            }
        }


        init {
            processExecutor.redirectOutput(ProcessLogAppender(STDType.Out, std.stream))
            processExecutor.redirectError(ProcessLogAppender(STDType.Err, std.stream))
            with(listener) {
                beforeStart += {
                    state = Starting
                }
                afterFinish += { _, result ->
                    info { "Process [${process.name}]stopped with exit code ${result.exitValue} for $state" }
                    state = when (result.exitValue) {
                        ExitCode.OK.code -> Completed
                        ExitCode.SigTerm.code,
                        ExitCode.SigKill.code -> Paused
                        else -> Failed
                    }.also {
                        info { "${process.name} changed state to $it" }
                        exitCode = ExitCode.fromInt(result.exitValue)
                        endTimes as MutableList += currentTimeMillis()
                        timer.map { timer -> timer.cancel() }
                    }

                    info { "Process stopped with exit code ${result.exitValue} for $state" }
                }
                afterStart += { _, _ ->
                    info { "Process ${process.name} about to start from state $state" }
                    startTimes as MutableList += currentTimeMillis()
                    timer = timer("ITasser counter timer", period = 500, initialDelay = 0) {
                        executionTimePrivate = durationFromExecutionTimes()
                    }.some()
                    startedTime = startTimes[0]
                    state = Running
                    info { "Process ${process.name} after starting with state $state" }

                }
            }
        }

        private fun durationFromExecutionTimes(): Long {
            return startTimes
                .subList(0, max(0, startTimes.size - 1))
                .mapIndexed { index, start ->
                    if (index <= endTimes.size - 1) abs(endTimes[index] - start) else null
                }.filterNotNull()
                .sum() + abs(currentTimeMillis() - startTimes.last())
        }

        internal fun start(): Outcome<StartedProcess> {
            return Try { processExecutor.start() }
                .toEither { e -> FailedStart(process.name, e).also { errors += it } }
                .map { process -> process.also { realProcess = Some(process) } }
                .map { process ->
                    sysProcess = Some(newStandardProcess(process.process))
                    future = Some(process.future)
                    process
                }
        }

        fun await(): Outcome<ProcessResult> = realProcess
            .toEither { ProcessError.NoProcessError("realProcess") }
            .flatMap { p ->
                Try { p.future.get() }
                    .toEither { Timeout("[${process.name}] timed out", it) }
            }

        @Synchronized
        fun awaitStart() {
            safeWait(1000) {
                safeWait(100)
                state is Running ||
                        state is Completed ||
                        state is Failed
            }
        }

        internal fun kill(timeoutMillis: Long = 0): Outcome<ExecutionContext> =
            sysProcess.toEither { NoProcess(process.name) }.flatMap { proc ->
                info { "Killing process ${process.name}" }
                Try {
                    Thread.sleep(timeoutMillis)
                    ProcessUtil.destroyGracefullyAndWait(proc, 10, TimeUnit.SECONDS)
                }.map { state = Stopping }
                    .toEither { e -> Timeout(this@ITasser.process.name, e).also { error -> errors += error } }
                    .mapLeft { error { "Killing process errored with $it" }; it }
                    .map { this }
            }

        fun destroy(): Outcome<ExecutionContext> = sysProcess.toEither {
            NoProcess(process.name)
        }.flatMap { process ->
            Try { ProcessUtil.destroyForcefullyAndWait(process, 10, TimeUnit.SECONDS) }
                .toEither { e -> Timeout(this@ITasser.process.name, e).also { error -> errors += error } }
                .map { this }
        }

        inner class ProcessLogAppender<T : STDType>(
            val std: T,
            val list: ObservableList<ProcessOutput>
        ) : LogOutputStream(), Logger {
            override fun processLine(p0: String) {
                info { "[STD$std] << capturing line [$p0] for runner [${process.name}][${process.id}]" }
                list += ProcessOutput(p0, DateTime.now(), std)
            }
        }

    }

    override fun toString(): String {
        return "ITasser(process=${process.name}, stateProperty=$state)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ITasser

        if (process.id != other.process.id) return false

        return true
    }

    override fun hashCode(): Int {
        return process.id.hashCode()
    }

    data class StartStop(val start: Long, val stop: Long? = null)

}
