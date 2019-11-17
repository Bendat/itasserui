package itasserui.app.mytasser.process.pane

import arrow.core.Either
import itasser.app.mytasser.app.process.pane.ProcessPane
import itasserui.app.mytasser.UserAppSpec
import itasserui.app.user.ProfileManager
import itasserui.common.interfaces.inline.RawPassword
import org.testfx.api.FxAssert
import org.testfx.matcher.control.ListViewMatchers

import java.time.Duration

import static itasser.app.mytasser.app.process.pane.ProcessPaneCss.*

class ProcessPaneSpec extends UserAppSpec<ProcessPane> {

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

    ArrayList args() {
        def ls = new ArrayList()
        ls.add(file.toAbsolutePath().toString())
        ls.add("-hello")
        ls.add("-world")
        return ls
    }

    void "Adding a process should be reflected in the queued tab"() {
        def ls = args()

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

    void "Verify max executing works correctly"() {
        def ls = args()

        given: "Locator for the running list"
        def runningLocator = { -> lookup(runningList.render()) }
        def queuedlocator = { -> lookup(getQueuedList().render()) }

        and: "Five processes"
        newProcess(ls, user)
        newProcess(ls, user)
        newProcess(ls, user)
        newProcess(ls, user)
        newProcess(ls, user)

        when: "Navigating to the running sequences tab"
        clickOn(runningTab.render())

        then: "List view should have 3 items"
        FxAssert.verifyThat(runningLocator(), ListViewMatchers.hasItems(3))
        FxAssert.verifyThat(queuedlocator(), ListViewMatchers.hasItems(2))
    }

}
