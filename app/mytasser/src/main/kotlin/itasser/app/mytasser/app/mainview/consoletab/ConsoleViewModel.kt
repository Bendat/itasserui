package itasser.app.mytasser.app.mainview.consoletab

import itasserui.lib.process.details.ExecutionState
import itasserui.lib.process.process.ITasser
import tornadofx.ItemViewModel

class ConsoleViewModel : ItemViewModel<ConsoleViewController>(ConsoleViewController()) {
    val selectedSequence = bind(ConsoleViewController::selectedSequenceProperty)
    val command = bind(ConsoleViewController::commandProperty)
    val stream = bind(ConsoleViewController::stdStream)
    val runPauseIcon = bind(ConsoleViewController::runPauseIconProperty)
    val stopIcon = bind(ConsoleViewController::stopIconProperty)
    val copyIcon = bind(ConsoleViewController::copyIcon)
    val executionStateProperty = bind(ConsoleViewController::executionStateProperty)
    fun setRunPlayIcon(state: ExecutionState) =
        item.setRunPlayIcon(state)

    fun perform(ifNot: () -> Unit, op: (ITasser) -> Unit) {
        item.selectedSequence?.let { seq ->
            item.profileManager.perform(seq.process.createdBy, ifNot) { op(seq) }
        }
    }
}