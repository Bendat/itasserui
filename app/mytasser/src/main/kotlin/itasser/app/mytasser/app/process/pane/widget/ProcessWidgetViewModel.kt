package itasser.app.mytasser.app.process.pane.widget

import itasserui.app.user.User
import itasserui.lib.process.process.ITasser
import tornadofx.ItemViewModel

class ProcessWidgetViewModel(user: User, process: ITasser) :
    ItemViewModel<ProcessWidgetController>(ProcessWidgetController(user, process)) {
    val username = bind(ProcessWidgetController::usernameProperty)
    val startedTimeFormatted = bind(ProcessWidgetController::startedTimeFormattedProperty)
    val startedTime = bind(ProcessWidgetController::startedTimeProperty)
    val sequenceIcon = bind(ProcessWidgetController::sequenceIconProperty)
    val runPauseIcon = bind(ProcessWidgetController::runPauseIconProperty)
    val stopIcon = bind(ProcessWidgetController::stopIconProperty)

    fun togglePlay() =
        item.onRunPauseClicked()
}