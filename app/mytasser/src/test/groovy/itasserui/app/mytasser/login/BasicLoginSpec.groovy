package itasserui.app.mytasser.login


import arrow.core.Some
import itasser.app.mytasser.app.login.LoginModal
import itasserui.app.user.ProfileManager
import javafx.scene.input.KeyCode
import org.testfx.api.FxAssert
import org.testfx.matcher.control.ComboBoxMatchers

import static itasserui.common.utils.SafeWaitKt.safeWait

class BasicLoginSpec extends LoginSpec {

    void "Create the admin user and log in"() {
        given:
        def timeoutTime = lookup("#user_timeout")
        def model = view.model

        when: "The required fields are filled"
        clickOn("#username_field").write(username)
        clickOn("#password_field").write(password)
        clickOn(".increment-arrow-button")
        clickOn("#timeout_unit")
        type(KeyCode.DOWN)
        type(KeyCode.ENTER)

        then: "The Timeout box to have a value of 1 second"
        timeoutTime.query().getValue() == 1
        FxAssert.verifyThat(timeUnit, ComboBoxMatchers.hasSelectedItem(LoginModal.LoginDuration.Seconds))


        expect:
        model.username.value == username
        model.password.value == password
        model.duration.value == 1

        when: "Clicking login"
        clickOn("#login_login")

        then: "The user is logged in"
        def session = view.model.userLogin.value as Some<ProfileManager.Session>
        session.t.active

        when: "Waiting 1/2 seconds"
        safeWait(500)

        then: "Verify user remains logged in"
        session.t.active
        session.t.sessionTimeRemaining <= 500

        when: "Waiting a further 1/2 seconds"
        safeWait(600)
        session.t.sessionTimeRemaining <= 0

        then: "Verify user session becomes inactive"
        !session.t.active
    }

}
