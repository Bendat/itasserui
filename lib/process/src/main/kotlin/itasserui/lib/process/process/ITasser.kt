@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package itasserui.lib.process.process

import arrow.core.*
import com.fasterxml.jackson.annotation.JsonIgnore
import itasserui.common.`typealias`.Outcome
import itasserui.common.extensions.format
import itasserui.common.logger.Logger
import itasserui.common.serialization.JsonObject
import itasserui.lib.process.*
import itasserui.lib.process.details.ExecutionState
import itasserui.lib.process.details.ExecutionState.Queued
import itasserui.lib.process.details.ExitCode
import itasserui.lib.process.details.TrackingList
import lk.kotlin.observable.property.StandardObservableProperty
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
    val errors = TrackingList<ProcessError>()
    @JsonIgnore
    val stateProperty = StandardObservableProperty(state)
    var state: ExecutionState by stateProperty
    @JsonIgnore
    val priorityProperty = StandardObservableProperty(priority)
    var priority: Int by priorityProperty
    val std = STD()
    @JsonIgnore
    val executor = ExecutionContext()


    private var timer: Option<Timer> = None
    private val startTimesPrivate = arrayListOf<Long>()
    val startTimes: List<Long> get() = startTimesPrivate
    private val endTimesPrivate = arrayListOf<Long>()
    val endTimes: List<Long> get() = endTimesPrivate


    val executionTimeProperty = StandardObservableProperty(0L)
    private var executionTimePrivate by executionTimeProperty
    val executionTime get() = executionTimePrivate


    inner class ExecutionContext {
        var future: Option<Future<ProcessResult>> = None
        private var realProcess: Option<StartedProcess> = None
        private var sysProcess: Option<SystemProcess> = None
        private val processExecutor: ProcessExecutor = ExecutorFactory.executor(process.args, listener)
        var exitCode: Option<ExitCode> = None

        init {
            processExecutor.redirectOutput(ProcessLogAppender(STDType.Out, std.output))
            processExecutor.redirectError(ProcessLogAppender(STDType.Err, std.err))
            with(listener) {
                afterFinish += { _, result ->
                    state = when (result.exitValue) {
                        ExitCode.OK.code -> ExecutionState.Completed
                        ExitCode.SigTerm.code,
                        ExitCode.SigKill.code -> ExecutionState.Paused
                        else -> ExecutionState.Failed
                    }.also {
                        exitCode = ExitCode.fromInt(result.exitValue)
                        endTimes as MutableList += currentTimeMillis()
                        timer.map { timer -> timer.cancel() }
                    }
                    info { "Process stopped with exit code ${result.exitValue} for $state" }
                }
                afterStart += { _, _ ->
                    info { "Process about to start from state $state" }
                    startTimes as MutableList += currentTimeMillis()
                    timer = timer("ITasser counter timer", period = 1000, initialDelay = 0) {
                        warn { "Executing timer whose old value is $executionTimePrivate" }
                        executionTimePrivate = durationFromExecutionTimes()
                        warn { "Exec time ${Duration.ofMillis(executionTime).format()}" }
                    }.some()
                    state = ExecutionState.Running
                    info { "Process after starting with state $state" }

                }
            }
        }

        private fun durationFromExecutionTimes(): Long {
            return startTimes
                .subList(0, max(0, startTimes.size - 1))
                .mapIndexed { index, start ->
                    abs(endTimes[index] - start)
                }.sum() + abs(currentTimeMillis() - startTimes.last())
        }

        fun start(): Outcome<StartedProcess> {
            return Try {
                processExecutor.start()
            }.toEither { e ->
                FailedStart(process.name, e).also { errors += it }
            }.map { process ->
                process.also { realProcess = Some(process) }
            }.map { process ->
                sysProcess = Some(newStandardProcess(process.process))
                future = Some(process.future)
                process
            }.also {
                info { "Start result is $it" }
            }
        }

        fun await(): Outcome<ProcessResult> = realProcess.toEither {
            ProcessError.NoProcessError("realProcess")
        }.flatMap { p ->
            Try {
                p.future.get()
            }.toEither {
                Timeout("[${process.name}] timed out", it)
            }
        }

        fun kill(): Outcome<ExecutionContext> =
            sysProcess.toEither { NoProcess(process.name) }.flatMap { proc ->
                info { "Killing process ${process.name}" }
                Try { ProcessUtil.destroyGracefullyAndWait(proc, 10, TimeUnit.SECONDS) }
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
            val list: TrackingList<String>
        ) : LogOutputStream(), Logger {
            override fun processLine(p0: String) {
                info { "[STD$std] << capturing line [$p0] for runner [${process.name}][${process.id}]" }
                list += p0
            }
        }

    }

    override fun toString(): String {
        return "ITasser(process=${process.name}, stateProperty=$state)"
    }

    data class StartStop(val start: Long, val stop: Long? = null)

}
