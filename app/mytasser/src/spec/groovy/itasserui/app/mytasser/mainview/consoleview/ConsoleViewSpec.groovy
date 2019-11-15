package itasserui.app.mytasser.mainview.consoleview

import itasser.app.mytasser.app.mainview.consoletab.ConsoleView
import itasser.app.mytasser.app.mainview.consoletab.EventShooter
import itasser.app.mytasser.app.mainview.consoletab.SelectedSequenceEvent
import itasser.app.mytasser.app.process.pane.widget.WidgetCss
import itasserui.app.mytasser.UserAppSpec
import itasserui.lib.process.process.ITasser
import org.testfx.api.FxAssert
import org.testfx.matcher.control.TextInputControlMatchers

import java.time.Duration

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
                datadir
        )
        event = new EventShooter(testScope)
        return new ConsoleView(testScope)
    }

    void "Verify Selected Sequence Event updates the view."() {
        when: "A new sequence event is fired"
        view.fire(new SelectedSequenceEvent(itasser))

        then:
        "The ConsoleView should represent ${itasser.process.name}"
        view.model.item.selectedSequence == itasser
    }

    void "Runs the sequence and verifies the output"() {
        given: "The user logs in"
        extractor.profile.login(user, account.password, Duration.ofMinutes(1))

        and: "Given the error text and command textfield nodes"
        def text = { -> lookup(".err-text").queryText() }
        def command = { -> lookup(".sequence-console-command") }
        when: "A new sequence event is fired"
        view.fire(new SelectedSequenceEvent(itasser))

        and: "The run button is clicked"
        clickOn(WidgetCss.controlButton.render())

        and: "We wait a for job to complete"
        itasser.executor.waitForFinish()

        then: "The error text should be displayed"
        text().text.contains("Error occurred in script")

        and: "The textfield should display the executed command"
        FxAssert.verifyThat(command(), TextInputControlMatchers.hasText(itasser.command))
    }

}
