package itasser.app.mytasser.app.process.pane

import itasserui.app.mytasser.lib.kInject
import itasserui.app.user.ProfileManager
import tornadofx.ItemViewModel

class ProcessPaneViewModel : ItemViewModel<ProcessPaneController>(ProcessPaneController()) {
    val profileManager: ProfileManager by kInject<ProfileManager>()
    val processes = bind(ProcessPaneController::processes)
    val running = bind(ProcessPaneController::running)
    val queued = bind(ProcessPaneController::queued)
    val completed = bind(ProcessPaneController::completed)
    val paused = bind(ProcessPaneController::paused)
    val stopping = bind(ProcessPaneController::stopping)
    val failed = bind(ProcessPaneController::failed)
}