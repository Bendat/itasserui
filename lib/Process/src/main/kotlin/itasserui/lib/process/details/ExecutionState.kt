package itasserui.lib.process.details

import com.fasterxml.jackson.annotation.JsonIgnore
import itasserui.common.utils.AbstractSealedObject

sealed class ExecutionState : AbstractSealedObject() {
    object Completed : ExecutionState()
    object Paused : ExecutionState()
    object Running : ExecutionState()
    object Queued : ExecutionState()
    object Failed : ExecutionState()

    @get:JsonIgnore
    val isRunnable
        get() = (this == Queued) or
                (this == Paused)

    override fun toString(): String {
        return javaClass.simpleName
    }

    companion object {
        val states
            get() = ExecutionState::class
                .sealedSubclasses
                .mapNotNull { it.objectInstance }

        operator fun get(name: String) = states.firstOrNull { it.simpleName == name }
    }
}