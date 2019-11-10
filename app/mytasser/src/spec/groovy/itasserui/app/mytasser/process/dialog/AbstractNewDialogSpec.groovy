package itasserui.app.mytasser.process.dialog

import itasser.app.mytasser.app.process.newDialog.NewProcessDialog
import itasser.app.mytasser.app.process.newDialog.NewSequenceCss as css
import itasserui.app.mytasser.UserAppSpec
import javafx.scene.control.TextInputControl

import static itasserui.common.utils.SafeWaitKt.safeWait

class AbstractNewDialogSpec extends UserAppSpec<NewProcessDialog> {

    @Override
    NewProcessDialog create() {
        setupStuff()
        return new NewProcessDialog(testScope)
    }
}

class BasicNewDialogSpec extends AbstractNewDialogSpec {

    void "Tapping create button without being logged in"() {
        given: "Dialog fields"
        def userField = lookup(".${css.userField.name}").queryComboBox()
        def createButton = lookup(".${css.createButton.name}").queryButton()
        and: "Login dialog fields"
        def loginUserField = { -> lookup("#username_field").queryComboBox() }
        def loginCancel = { -> lookup("#login_cancel").queryButton() }

        when: "Entering the user name and clicking create"
        clickOn(userField).write(user.username.value)
        clickOn(createButton)

        then: "A login dialog should appear with the same username autofille"
        userField.value == user.username.value
        loginUserField().value == userField.value

        cleanup:
        clickOn(loginCancel())
    }

    void "Logging in and creating invalid process"() {
        given: "Dialog fields"
        def userField = lookup(".${css.userField.name}").queryComboBox()
        def createButton = lookup(".${css.createButton.name}").queryButton()
        def errorPrompt = {->lookup(".${css.errorLabel.name}").queryLabeled()}

        and: "Login dialog fields"
        def loginUserPassword = { -> lookup("#password_field").queryTextInputControl() }

        when: "Logging in with valid credentials"
        clickOn(userField).write(user.username.value)
        clickOn(createButton)
        loginWithModal(loginUserPassword)
        clickOn("#login_login")

        then: "Verify creation fails because necessary fields are empty"
        errorPrompt().isVisible()
        safeWait(10000)
    }


    void "Creates a valid process"() {
        expect:
        safeWait(33000)
    }


    private void loginWithModal(Closure<TextInputControl> loginUserPassword) {
        clickOn(loginUserPassword()).write(account.password.value)
        for (int i = 0; i < 30; i++) {
            clickOn(".login-modal .increment-arrow-button")
        }
    }
}