package itasser.app.mytasser.app.process.pane

import tornadofx.cssclass

object ProcessPaneCss {
    @JvmStatic
    val newButton by cssclass("process-pane-new-button")
    @JvmStatic
    val autoRunToggle by cssclass("process-pane-autorun-toggle")
    @JvmStatic
    val maxExecuting by cssclass("process-pane-max-executing-field")
    @JvmStatic
    val runningTab by cssclass("running-tab")
    @JvmStatic
    val runningList by cssclass("running-list")
    @JvmStatic
    val completedTab by cssclass("completed-tab")
    @JvmStatic
    val completedList by cssclass("completed-list")
    @JvmStatic
    val queuedTab by cssclass("process-pane-queued-tab")
    @JvmStatic
    val queuedList by cssclass("queued-list")
    @JvmStatic
    val failedTab by cssclass("process-pane-failed-tab")
    @JvmStatic
    val failedList by cssclass("failed-list")
    @JvmStatic
    val pausedTab by cssclass("process-pane-paused-tab")
    @JvmStatic
    val pausedList by cssclass("paused-list")
}