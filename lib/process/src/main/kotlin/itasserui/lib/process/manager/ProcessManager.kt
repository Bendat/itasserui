@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package itasserui.lib.process.manager

import arrow.core.toOption
import itasserui.common.extensions.addUpdatable
import itasserui.common.logger.Logger
import itasserui.lib.process.ArgNames
import itasserui.lib.process.details.ExecutionState
import itasserui.lib.process.process.CCRCProcess
import itasserui.lib.process.process.ITasser
import itasserui.lib.process.process.ITasserListener
import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.list.filtering
import lk.kotlin.observable.list.observableListOf
import lk.kotlin.observable.property.StandardObservableProperty
import java.nio.file.Path
import java.util.*

class ProcessManager(var maxExecuting: Int = 3, autoRun: Boolean = true) : Logger, AutoCloseable {

    val process = Processes()

    val autorunProperty = StandardObservableProperty(autoRun)
    var autoRun by autorunProperty

    private val defaultArgs = arrayOf(ArgNames.Perl, ArgNames.AutoFlush).map(Any::toString)

    fun new(
        processId: UUID,
        priority: Int,
        seqFile: Path,
        name: String,
        args: List<String>,
        createdBy: UUID,
        dataDir: Path,
        state: ExecutionState = ExecutionState.Queued
    ): ITasser {
        val fullArgs = defaultArgs
            .toMutableList()
            .apply { addAll(args) }
        return ITasser(
            CCRCProcess(
                id = processId,
                seq = seqFile,
                name = name,
                args = fullArgs,
                createdAt = Date(),
                createdBy = createdBy,
                dataDir = dataDir
            ),
            priority = priority,
            state = state,
            listener = getListener(name, processId)
        ).also {
            info { "Created new process ${it.toJson(3)}" }
            process += it
            if (process.running.size < maxExecuting && autoRun) {
                process.next.map { proc ->
                    proc.executor.start()
                    trace { "Starting process [${proc.process.name}]" }
                }
            }
        }
    }

    override fun close() {
        process.processes.forEach {
            it.executor.kill()
        }
    }

    private fun getListener(name: String, id: UUID) =
        ITasserListener().apply {
            afterFinish { _, _ ->
                trace { "A process $name::$id has finished" }
                if (autoRun) {
                    process.next.map {
                        trace { "Starting process [${it.process.name}]" }
                        it.executor.start()
                    }
                }
            }
        }

    @Suppress("MemberVisibilityCanBePrivate")
    inner class Processes {
        val processes = observableListOf<ITasser>()
        val queued: ObservableList<ITasser> = processes.filtering { it.state is ExecutionState.Queued }
        val paused: ObservableList<ITasser> = processes.filtering { it.state is ExecutionState.Paused }
        val completed: ObservableList<ITasser> = processes.filtering { it.state is ExecutionState.Completed }
        val running: ObservableList<ITasser> = processes.filtering { it.state is ExecutionState.Running }
        val failed: ObservableList<ITasser> = processes.filtering { it.state is ExecutionState.Failed }

        val size
            get() = processes.size

        val next
            get() = queued
                .minBy { it.priority }
                .toOption()

        operator fun get(id: UUID) = processes
            .first { it.process.id == id }
            .toOption()

        operator fun get(state: ExecutionState) = when (state) {
            is ExecutionState.Queued -> queued
            is ExecutionState.Paused -> paused
            is ExecutionState.Completed -> completed
            is ExecutionState.Failed -> failed
            is ExecutionState.Running -> running
        }

        operator fun plusAssign(process: ITasser) {
            processes.addUpdatable(process) { it.stateProperty }
        }
    }
}