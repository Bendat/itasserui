package itasser.app.mytasser.app.process.pane.widget

import itasserui.app.user.User
import itasserui.lib.process.details.ExecutionState
import itasserui.lib.process.process.ITasser
import tornadofx.ItemViewModel

class ProcessWidgetViewModel(user: User, process: ITasser) :
    ItemViewModel<ProcessWidgetController>(ProcessWidgetController(user, process)) {
    val username = bind(ProcessWidgetController::usernameProperty)
    val runPauseIcon = bind(ProcessWidgetController::runPauseIconProperty)
    val stopIcon = bind(ProcessWidgetController::stopIconProperty)
    val executionStateProperty = bind(ProcessWidgetController::executionStateProperty)
    val executionTimeProperty = bind(ProcessWidgetController::executionTimeProperty)
    val startTime = bind(ProcessWidgetController::startedTimeProperty)
    fun setRunPlayIcon(state: ExecutionState) =
        item.setRunPlayIcon(state)
}