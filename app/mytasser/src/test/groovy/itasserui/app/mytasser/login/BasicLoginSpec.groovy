package itasserui.app.mytasser.login

import arrow.core.Either
import itasser.app.mytasser.app.login.LoginModal
import itasserui.app.user.ProfileManager
import itasserui.app.user.UnregisteredUser
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import javafx.scene.input.KeyCode
import org.testfx.api.FxAssert
import org.testfx.matcher.control.ComboBoxMatchers

import static itasserui.common.utils.SafeWaitKt.safeWait

class BasicLoginSpec extends LoginSpec {

    void "Create the admin user"() {
        setup:
        def timeUnit = lookup("#timeout_unit")
        def timeoutTime = lookup("#user_timeout")
        def user = new UnregisteredUser(new Username(username), new RawPassword(password), new EmailAddress(email)).toUser(UUID.randomUUID())
        def model = view.model

        expect:
        "The combobox to have ${LoginModal.LoginDuration.values().size()}"
        FxAssert.verifyThat(timeUnit, ComboBoxMatchers.hasItems(4))
        FxAssert.verifyThat(timeUnit, ComboBoxMatchers.hasSelectedItem(LoginModal.LoginDuration.Actions))

        and:
        "A new user $username to exist in the database"
        view.model.item.profileManager.database.launch()
        view.model.item.profileManager.saveToDb(user)

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
        def session = view.model.userLogin.value as Either.Right<ProfileManager.Session>
        session.b.active

        when: "Waiting 1/2 seconds"
        safeWait(500)

        then: "Verify user remains logged in"
        session.b.active

        when: "Waiting a further 1/2 seconds"
        safeWait(600)

        then: "Verify user session becomes inactive"
        !session.b.active
    }

}
