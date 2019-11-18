package itasser.app.mytasser.app.mainview.consoletab

import tornadofx.cssclass
import tornadofx.cssid

object ConsoleViewCss {
    @JvmStatic
    val consoleCommandField by cssclass("console-view-command-field")
    @JvmStatic
    val consoleTextFlow by cssclass("console-view-output-text")
    @JvmStatic
    val consoleRunButton by cssclass("console-view-run-button")
    @JvmStatic
    val consoleStopButton by cssclass("console-view-stop-button")
    @JvmStatic
    val consoleCopyButton by cssclass("console-view-copy-button")
    @JvmStatic
    val consoleErrorText by cssclass("console-view-error-text")

}