package itasserui.app.mytasser.mainview.splitview

import itasser.app.mytasser.app.events.EventShooter
import itasser.app.mytasser.app.mainview.SplitView
import itasserui.app.mytasser.UserAppSpec
import javafx.scene.input.KeyCode
import org.testfx.api.FxAssert
import org.testfx.matcher.control.ListViewMatchers

import java.nio.file.Files
import java.time.Duration

import static itasser.app.mytasser.app.mainview.consoletab.ConsoleViewCss.INSTANCE as consolecss
import static itasser.app.mytasser.app.process.newDialog.NewProteinDialogCss.INSTANCE as newcss
import static itasser.app.mytasser.app.process.pane.ProcessPaneCss.INSTANCE as panecss
import static itasser.app.mytasser.app.process.pane.ProcessPaneCss.getAutoRunToggle

class SplitViewIntegrationSpec extends UserAppSpec<SplitView> {
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
        def fastafolder = tmpdirPath.resolve("fasta")
        Files.createDirectories(fastafolder)
        def fasta = tmpdirPath.resolve("seq.fasta")
        extractor.settings.itasser.pkgDir = file.parent
        Files.createFile(fasta)

        given: "A user"
        def user = account

        and: "A locator for password input"
        def loginUserPassword = { -> lookup("#password_field").queryTextInputControl() }
        def procId = { -> extractor.proc.processes.queued.first().process.id }

        when: "Disabling autorun"
        clickOn(autoRunToggle.render())

        and: "Clicking the create new protein sequence buton"
        clickOn(panecss.newButton.render())

        and: "A user is selected"
        clickOn(newcss.userField.render()).write(user.username.value)
        type(KeyCode.ENTER)

        and: "The protein details are entered"
        clickOn(newcss.description.render())
        clickOn(newcss.name.render()).write("Amoxylinezine")
        clickOn(newcss.description.render()).write("This isn't gonna work")
        clickOn(newcss.fastaLocation.render()).write(fastafolder.toString())
        clickOn(newcss.createButton.render())

        and: "We log in"
        loginWithModal(loginUserPassword, account, false)

        and: "We Navigate to the queued list"
        clickOn(panecss.queuedTab.render())
        clickOn(".${procId()}")

        and: "The control button is clicked from console view"
        clickOn(consolecss.consoleRunButton.render())

        and: "The process finishes"
        extractor.proc.processes.all.first().executor.waitForFinish()

        then:
        def tab = lookup(panecss.completedList.render())
        clickOn(panecss.completedTab.render())
        FxAssert.verifyThat(tab, ListViewMatchers.hasItems(1))

    }
}
