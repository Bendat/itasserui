package itasserui.lib.process.details

import arrow.core.Option
import arrow.core.toOption
import itasserui.common.logger.Logger
import itasserui.common.utils.AbstractSealedObject
import itasserui.lib.process.details.ExecutionState.*

sealed class ExitCode(
    val code: Int,
    val state: ExecutionState
) : AbstractSealedObject(), Logger {
    object OK : ExitCode(0, Completed)
    object CtrlC : ExitCode(130, Paused)
    object SigTerm : ExitCode(143, Paused)
    object SigKill : ExitCode(137, Paused)
    object Error : ExitCode(1, Failed)

    companion object : Logger {
        fun fromInt(code: Int): Option<ExitCode> {
            return ExitCode::class.sealedSubclasses.map { it.objectInstance }.firstOrNull { inst ->
                info { "Exit code is [$code]" }
                inst?.code == code
            }.toOption()
        }
    }

}