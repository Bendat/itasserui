package itasserui.app.mytasser.mainview.splitview

import itasser.app.mytasser.app.mainview.SplitView
import itasser.app.mytasser.app.mainview.consoletab.EventShooter
import itasserui.app.mytasser.UserAppSpec
import itasserui.common.utils.SafeWaitKt
import itasserui.lib.process.process.ITasser

import java.time.Duration

class SplitViewIntegrationSpec extends UserAppSpec<SplitView> {
    ITasser itasser = null
    EventShooter event = null

    @Override
    SplitView create() {
        setupStuff()
        def ls = new ArrayList()
        ls.add(file.toAbsolutePath().toString())
        ls.add("-hello")
        ls.add("-world")
        println("File is $file")
        event = new EventShooter(testScope)
        extractor.profile.login(user.username, account.password, Duration.ofMinutes(5))
        return new SplitView(testScope)
    }

    void "Create, run and complete a new process"() {
//        when:
//
//        then:
    }
}
