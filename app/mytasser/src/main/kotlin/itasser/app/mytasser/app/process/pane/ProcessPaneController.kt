package itasser.app.mytasser.app.process.pane

import itasserui.app.mytasser.lib.extensions.bind
import itasserui.app.mytasser.lib.extensions.toFx
import itasserui.app.mytasser.lib.kInject
import itasserui.lib.process.manager.ProcessManager
import javafx.collections.FXCollections.observableArrayList
import tornadofx.Controller

class ProcessPaneController : Controller() {
    val processManager: ProcessManager by kInject()
    private val procs = processManager.processes

    val autoRun = processManager.autorunProperty.toFx()
    val maxExecuting = processManager.maxExecutingProperty.toFx()
    val processes = observableArrayList(procs.all).bind(procs.all)
    val running = observableArrayList(procs.running).bind(procs.running)
    val queued = observableArrayList(procs.queued).bind(procs.queued)
    val completed = observableArrayList(procs.completed).bind(procs.completed)
    val paused = observableArrayList(procs.paused).bind(procs.paused)

    val stopping = observableArrayList(procs.stopping).bind(procs.stopping)
    val failed = observableArrayList(procs.failed).bind(procs.failed)

}
