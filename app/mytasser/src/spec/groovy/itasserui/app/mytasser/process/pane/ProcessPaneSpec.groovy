package itasserui.app.mytasser.process.pane

import arrow.core.Either
import itasser.app.mytasser.app.process.pane.ProcessPane
import itasser.app.mytasser.app.process.pane.widget.WidgetCss
import itasserui.app.mytasser.UserAppSpec
import itasserui.app.user.ProfileManager
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.utils.SafeWaitKt
import itasserui.lib.process.process.ITasser

import java.time.Duration

class ProcessPaneSpec extends UserAppSpec<ProcessPane> {
    ITasser itasser = null

    @Override
    ProcessPane create() {
        setupStuff()
        println("Dir is ${tmpdirPath.toAbsolutePath()}")
        extractor.profile.login(user, account.password as RawPassword, Duration.ofSeconds(0)) as Either.Right<ProfileManager.Profile>
        def view = new ProcessPane(testScope)
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
                datadir
        )
        return view
    }

    void "Basic interactions with existing process"() {
        given:
        def id = itasser.process.id
        SafeWaitKt.safeWait(2500)
        expect:
        clickOn(".queued-tab")
        lookup(".queued-list").queryListView().items.size() == 1

        when:
        clickOn(".$id .${WidgetCss.controlButton.name}")
        login()

        then:
        clickOn(".running-tab")
        lookup(".running-list").queryListView().items.size() == 1

        and:
        clickOn(".queued-tab")
        clickOn(".running-tab")

        clickOn(".$id .${WidgetCss.controlButton.name}")
        clickOn(".paused-tab")

        then:
        lookup(".paused-list").queryListView().items.size() == 1
    }
}
