@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package itasserui.lib.process.manager

import arrow.core.firstOrNone
import arrow.core.toOption
import itasserui.common.extensions.addUpdatableProperty
import itasserui.common.logger.Logger
import itasserui.lib.process.ArgNames
import itasserui.lib.process.details.ExecutionState
import itasserui.lib.process.details.ExecutionState.*
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

class ProcessManager(
    maxExecuting: Int = 3,
    autoRun: Boolean = true
) : Logger, AutoCloseable {
    val processes = Processes()
    val maxExecutingProperty = StandardObservableProperty(maxExecuting)
    val maxExecuting by maxExecutingProperty
    val autorunProperty = StandardObservableProperty(autoRun)
    var autoRun by autorunProperty
    private val defaultArgs = arrayOf(ArgNames.Perl, ArgNames.AutoFlush).map(Any::toString)
    fun run(itasser: ITasser) {
        info{"Executing itasser"}
        when {
            itasser.state == Running -> itasser.executor.kill()
            processes.running.size < maxExecuting -> itasser.executor.start()
            else -> itasser.priority++
        }
    }

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            processes.all.forEach {
                it.executor.destroy()
            }
        })
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
        outDir: Path = dataDir,
        state: ExecutionState = Queued
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
                dataDir = dataDir,
                outDir = outDir
            ),
            priority = priority,
            state = state,
            listener = getListener(name, processId)
        ).also {
            info { "Created new process ${it.toJson(3)}" }
            processes += it
            if (autoRun)
                run(it)
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
                    info { "Running next process ${processes.next}" }
                    processes.next.map { run(it) }
                }
                res.exitValue
            }
        }


    @Suppress("MemberVisibilityCanBePrivate")
    inner class Processes {
        private val sorter = fun(
            first: ITasser,
            second: ITasser
        ): Boolean {
            return if (first.priority == second.priority) {
                val comp1 = first.process.createdAt
                val comp2 = second.process.createdAt
                when {
                    comp1 == comp2 -> true
                    comp1 < comp2 -> true
                    else -> false
                }
            } else {
                when {
                    first.priority > second.priority -> true
                    else -> false
                }
            }
        }
        val all = observableListOf<ITasser>()
        val queued: ObservableList<ITasser> = all.filtering { it.state is Queued }
            .sorting(sorter)
        val paused: ObservableList<ITasser> = all.filtering { it.state is Paused }
            .sorting(sorter)
        val completed: ObservableList<ITasser> = all.filtering { it.state is Completed }
            .sorting(sorter)
        val running: ObservableList<ITasser> = all.filtering { it.state is Running || it.state == Starting }
            .sorting(sorter)
        val stopping: ObservableList<ITasser> = all.filtering { it.state is Stopping }
            .sorting(sorter)
        val failed: ObservableList<ITasser> = all.filtering { it.state is Failed }
            .sorting(sorter)


        fun ObservableList<ITasser>.reloadOn(obj: ITasser, binds: ObservableProperty<ExecutionState>) {
            binds += {
                remove(obj)
                add(obj)
            }
        }

        val size
            get() = all.size

        val next
            get() = queued.firstOrNone()

        val nextRunning
            get() = running
                .filter { it.state is Running }
                .map { info { "Selecting next ${it.process.name} with state ${it.state}" }; it }
                .minBy { it.priority }
                .toOption()

        operator fun get(id: UUID) = all
            .first { it.process.id == id }
            .toOption()

        operator fun get(state: ExecutionState) = when (state) {
            is Queued -> queued
            is Paused -> paused
            is Failed -> failed
            is Running -> running
            is Stopping -> stopping
            is Completed -> completed
            is Starting -> running.filtering { it.state is Starting }
        }

        operator fun plusAssign(process: ITasser) {
            all.addUpdatableProperty(process) { it.stateProperty }
        }
    }
}