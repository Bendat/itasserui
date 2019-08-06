package itasserui.lib.process.process

import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.listener.ProcessListener

object ExecutorFactory {
    fun executor(command: List<String>, listener: ProcessListener): ProcessExecutor =
        ProcessExecutor().command(command).readOutput(true).listener(listener)
}