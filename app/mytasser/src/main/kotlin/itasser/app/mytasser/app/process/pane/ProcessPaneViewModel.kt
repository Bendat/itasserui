package itasser.app.mytasser.app.process.pane

import itasser.app.mytasser.app.process.pane.widget.ProcessWidgetController
import itasser.app.mytasser.lib.kInject
import itasserui.app.user.ProfileManager
import tornadofx.ItemViewModel

class ProcessPaneViewModel : ItemViewModel<ProcessPaneController>(ProcessPaneController()) {
    val profileManager: ProfileManager by kInject()
    val autoRun = bind(ProcessPaneController::autoRun)
    val maxExecuting = bind(ProcessPaneController::maxExecuting)
    val processes = bind(ProcessPaneController::processes)
    val running = bind(ProcessPaneController::running)
    val queued = bind(ProcessPaneController::queued)
    val completed = bind(ProcessPaneController::completed)
    val paused = bind(ProcessPaneController::paused)
    val stopping = bind(ProcessPaneController::stopping)
    val failed = bind(ProcessPaneController::failed)
    val dnaIcon = bind(ProcessPaneController::dnaIconProperty)
    val queuedIcon = bind(ProcessPaneController::queuedIconProperty)
    val completedIcon = bind(ProcessPaneController::completedIconProperty)
    val runningIcon = bind(ProcessPaneController::runningIconProperty)
    val failedIcon = bind(ProcessPaneController::failedIconProperty)
    val pausedIcon = bind(ProcessPaneController::stoppedIconProperty)

}