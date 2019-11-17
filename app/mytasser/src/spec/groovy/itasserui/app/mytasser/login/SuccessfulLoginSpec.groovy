package itasserui.app.mytasser.login

import itasser.app.mytasser.app.components.ControlsCss
import itasser.app.mytasser.app.login.LoginModal
import itasserui.app.mytasser.UserAppSpec
import javafx.scene.input.KeyCode

import static itasser.app.mytasser.app.login.LoginModalCss.INSTANCE as css
import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.control.ComboBoxMatchers.hasSelectedItem

class SuccessfulLoginSpec extends UserAppSpec<LoginModal> {
    void "There should be no users logged in"() {
        expect:
        extractor.profile.activeSessions.size() == 0
    }

    void "Verify selecting the user"() {
        given: "The viewmodel"
        def model = view.model

        when: "Clicking on the username field and hits enter"
        clickOn(css.loginUsernameField.render()).write(username)
        type(KeyCode.ENTER)

        then: "Then the view model should contain a profile"
        verifyThat(lookup("#timeout_unit"), hasSelectedItem(LoginModal.LoginDuration.Seconds))

        model.username.value == username
    }

    void "Logs the user in"() {
        setup: "The timeout duration text"
        extractor.profile.activeSessions.size() == 0

        when: "The user credentials are entered"
        clickOn(css.loginUsernameField.render()).write(account.username.value)
        clickOn(css.loginPasswordField.render()).write(account.password.value)

        and: "And the session duration is set to 1 minute"
        clickOn(ControlsCss.INSTANCE.spinnerIncrement.render())
        clickOn(css.loginTimeoutUnitCombo.render())
        type(KeyCode.DOWN)
        type(KeyCode.ENTER)

        and: "The login button is clicked"
        clickOn(css.loginLoginButton.render())

        then: "The user should be logged in"
        extractor.profile.activeSessions.size() == 1
    }

    @Override
    LoginModal create() {
        setupStuff()
        return new LoginModal("Login Test", testScope)
    }
}
