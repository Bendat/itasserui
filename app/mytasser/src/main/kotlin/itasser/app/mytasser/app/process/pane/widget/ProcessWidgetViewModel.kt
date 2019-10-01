package itasser.app.mytasser.app.process.pane.widget

import tornadofx.ItemViewModel

class ProcessWidgetViewModel : ItemViewModel<ProcessWidgetController>(ProcessWidgetController()) {
    val userIcon = bind(ProcessWidgetController::userIconProperty)
    val username = bind(ProcessWidgetController::usernameProperty)
    val startedTimeIcon = bind(ProcessWidgetController::startedTimeIconProperty)
    val startedTime = bind(ProcessWidgetController::startedTimeProperty)
    val sequenceIcon = bind(ProcessWidgetController::sequenceIconProperty)
    val sequenceName = bind(ProcessWidgetController::sequenceNameProperty)
    val runPauseIcon = bind(ProcessWidgetController::runPauseIconProperty)
    val stopIcon = bind(ProcessWidgetController::stopIconProperty)
}