package itasserui.app.mytasser.mainview.stage

import itasser.app.mytasser.app.mainview.consoletab.EventShooter
import itasser.app.mytasser.app.mainview.consoletab.SelectedSequenceEvent
import itasser.app.mytasser.views.MainView
import itasserui.app.mytasser.UserAppSpec
import itasserui.common.utils.SafeWaitKt
import itasserui.lib.process.details.ExecutionState
import itasserui.lib.process.process.ITasser

import java.time.Duration

class StageViewSpec extends UserAppSpec<MainView> {
    ITasser itasser = null
    EventShooter event = null

    @Override
    MainView create() {
        setupStuff()
        def ls = new ArrayList()
        ls.add(file.toAbsolutePath().toString())
        ls.add("-hello")
        ls.add("-world")
        println("File is $file")
        itasser = extractor.proc.new(
                UUID.randomUUID(),
                0,
                file,
                "Hello",
                ls,
                user.id,
                datadir,
                datadir,
                ExecutionState.Queued.INSTANCE
        )
        event = new EventShooter(testScope)
        extractor.profile.login(user.username, account.password, Duration.ofMinutes(5))
        return new MainView(testScope)
    }

    void "Lets see"() {
        given:
//        event.fire(new SelectedSequenceEvent(itasser))
        SafeWaitKt.safeWait(1000)
//        itasser.executor.start$process()

        expect:
        SafeWaitKt.safeWait(90000)
    }
}
