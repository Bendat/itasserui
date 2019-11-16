package itasserui.app.mytasser.process.pane

import arrow.core.Either
import itasser.app.mytasser.app.process.pane.ProcessPane
import itasser.app.mytasser.app.process.pane.widget.WidgetCss
import itasserui.app.mytasser.UserAppSpec
import itasserui.app.user.ProfileManager
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.utils.SafeWaitKt
import itasserui.lib.process.process.ITasser
import org.testfx.api.FxAssert
import org.testfx.matcher.control.ListViewMatchers

import java.time.Duration

import static itasser.app.mytasser.app.process.pane.ProcessPaneCss.*

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
        return view
    }

    void "Adding a process should be reflected in the queued tab"() {
        def ls = new ArrayList()
        ls.add(file.toAbsolutePath().toString())
        ls.add("-hello")
        ls.add("-world")

        given: "Locator for the queued list"

        def queuedlocator = { -> lookup(getQueuedList().render()) }
        when: "Auto run is disabled"
        clickOn(autoRunToggle.render())

        and: "A new process is created"
        newProcess(ls, user)

        and: "The queued tab is opened"
        clickOn(queuedTab.render())

        then:
        FxAssert.verifyThat(queuedlocator(), ListViewMatchers.hasItems(1))
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
