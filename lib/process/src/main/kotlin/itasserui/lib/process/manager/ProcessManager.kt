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
import lk.kotlin.observable.list.sorting
import lk.kotlin.observable.property.ObservableProperty
import lk.kotlin.observable.property.StandardObservableProperty
import lk.kotlin.observable.property.plusAssign
import java.nio.file.Path
import java.util.*

class ProcessManager(var maxExecuting: Int = 3, autoRun: Boolean = true) : Logger, AutoCloseable {

    val processes = Processes()

    val autorunProperty = StandardObservableProperty(autoRun)
    var autoRun by autorunProperty

    private val defaultArgs = arrayOf(ArgNames.Perl, ArgNames.AutoFlush).map(Any::toString)
    fun run(itasser: ITasser) {
        if (processes.running.size < maxExecuting && autoRun)
            itasser.executor.start()
        else itasser.priority++
    }

    @JvmOverloads
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
            processes += it
            if (processes.running.size < maxExecuting && autoRun) {
                processes.next.map { proc ->
                    proc.executor.start()
                    trace { "Starting process [${proc.process.name}]" }
                }
            }
        }
    }

    override fun close() {
        processes.all.forEach {
            it.executor.kill()
        }
    }

    private fun getListener(name: String, id: UUID) =
        ITasserListener().apply {
            afterFinish { _, res ->
                trace { "A process $name::$id has finished" }
                if (autoRun) {
                    processes.next.map {
                        debug { "Starting process [${it.process.name}]" }
                        it.executor.start()
                    }
                }
                res.exitValue
            }
        }

    @Suppress("MemberVisibilityCanBePrivate")
    inner class Processes {
        val all = observableListOf<ITasser>()
        val queued: ObservableList<ITasser> =
            all.filtering { it.state is ExecutionState.Queued }
        val paused: ObservableList<ITasser> = all.filtering { it.state is ExecutionState.Paused }
        val completed: ObservableList<ITasser> = all.filtering { it.state is ExecutionState.Completed }
        val running: ObservableList<ITasser> = all.filtering { it.state is ExecutionState.Running }
        val failed: ObservableList<ITasser> = all.filtering { it.state is ExecutionState.Failed }

        fun ObservableList<ITasser>.reloadOn(obj: ITasser, binds: ObservableProperty<ExecutionState>) {
            binds += {
                remove(obj)
                add(obj)
            }
        }

        val size
            get() = all.size

        val next
            get() = queued
                .map { info { "Selecting next ${it.process.name} with state ${it.state}" }; it }
                .minBy { it.priority }
                .toOption()

        val nextRunning
            get() = running
                .filter { it.state is ExecutionState.Running }
                .map { info { "Selecting next ${it.process.name} with state ${it.state}" }; it }
                .minBy { it.priority }
                .toOption()

        operator fun get(id: UUID) = all
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
            all.addUpdatable(process) {
                listOf(it.stateProperty, it.priorityProperty)
            }
        }
    }
}