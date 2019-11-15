package itasserui.app.mytasser.login


import arrow.core.Some
import itasser.app.mytasser.app.login.LoginModal
import itasserui.app.mytasser.UserAppSpec
import itasserui.app.user.ProfileManager
import javafx.scene.input.KeyCode
import org.testfx.api.FxAssert
import org.testfx.matcher.control.ComboBoxMatchers

import static itasserui.common.utils.SafeWaitKt.safeWait
import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.control.ComboBoxMatchers.hasSelectedItem

class BasicLoginSpec extends UserAppSpec<LoginModal> {
    void "There should be no users logged in"() {
        expect:
        extractor.profile.activeSessions.size() == 0
    }

    void "Verify selecting the user"() {
        given: "The viewmodel"
        def model = view.model

        when: "Clicking on the username field and hits enter"
        clickOn("#username_field").write(username)
        type(KeyCode.ENTER)

        then: "Then the view model should contain a profile"
        verifyThat(lookup("#timeout_unit"), hasSelectedItem(LoginModal.LoginDuration.Seconds))

        model.username.value == username
    }

    void "Logs the user in"() {
        setup: "The timeout duration text"
        extractor.profile.activeSessions.size() == 0

        when: "The user credentials are entered"
        clickOn("#username_field").write(account.username.value)
        clickOn("#password_field").write(account.password.value)

        and: "And the session duration is set to 1 minute"
        clickOn(".increment-arrow-button")
        clickOn("#timeout_unit")
        type(KeyCode.DOWN)
        type(KeyCode.ENTER)

        and: "The login button is clicked"
        clickOn("#login_login")

        then: "The user should be logged in"
        extractor.profile.activeSessions.size() == 1
    }

    void "Fails due to bad password"(){
        when: "The user credentials are entered"
        clickOn("#username_field").write(account.username.value)
        clickOn("#password_field").write("womp womp")

        and: "And the session duration is set to 1 minute"
        clickOn(".increment-arrow-button")
        clickOn("#timeout_unit")
        type(KeyCode.DOWN)
        type(KeyCode.ENTER)

        and: "The login button is clicked"
        clickOn("#login_login")

        then: "The user should be logged in"
        alertContent().contains("Wrong Password")
        extractor.profile.activeSessions.size() == 0
    }

    void "Dismisses the bad password alert"() {
        when: "The user credentials are entered"
        clickOn("#username_field").write(account.username.value)
        clickOn("#password_field").write("womp womp")

        and: "And the session duration is set to 1 minute"
        clickOn(".increment-arrow-button")
        clickOn("#timeout_unit")
        type(KeyCode.DOWN)
        type(KeyCode.ENTER)

        and: "The login button is clicked"
        clickOn("#login_login")
        clickOn("OK")
        then: "Nothin'"
    }


    @Override
    LoginModal create() {
        setupStuff()
        return new LoginModal("Login Test", testScope)
    }
}
