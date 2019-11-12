package itasserui.app.mytasser.mainview.consoleview

import itasser.app.mytasser.app.mainview.consoletab.ConsoleView
import itasser.app.mytasser.app.mainview.consoletab.EventShooter
import itasserui.app.mytasser.UserAppSpec
import itasserui.common.utils.SafeWaitKt
import itasserui.lib.process.process.ITasser

class ConsoleViewSpec extends UserAppSpec<ConsoleView> {
    EventShooter event = null
    ITasser itasser = null

    @Override
    ConsoleView create() {
        setupStuff()
        def ls = new ArrayList()
        ls.add(file.toAbsolutePath().toString())
        ls.add("-hello")
        ls.add("-world")
        itasser = extractor.proc.new(
                UUID.randomUUID(),
                0,
                file,
                file.fileName.toString(),
                ls,
                user.id,
                dataDir
        )
        event = new EventShooter(testScope)
        return new ConsoleView(testScope)
    }

    void "Initial test"() {
        given:
        view.model.item.selectedSequence = itasser

        expect:
        itasser.executor.start$process()
        SafeWaitKt.safeWait(100000)
    }
}
