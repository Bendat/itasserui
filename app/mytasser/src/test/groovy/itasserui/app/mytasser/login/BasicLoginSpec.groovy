package itasserui.app.mytasser.login

import arrow.core.Either
import itasser.app.mytasser.app.login.LoginDuration
import itasserui.app.user.UnregisteredUser
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import javafx.scene.input.KeyCode
import org.testfx.api.FxAssert
import org.testfx.matcher.control.ComboBoxMatchers

class BasicLoginSpec extends LoginSpec {
    void "Create the admin user"() {
        given:
        def timeUnit = lookup(".timeout_unit")
        def user = new UnregisteredUser(new Username(username), new RawPassword(password), new EmailAddress(email)).toUser(UUID.randomUUID())

        when:
        clickOn(".username_field").write(username)
        clickOn(".password_field").write(password)
        clickOn(".increment-arrow-button")
        clickOn(".timeout_unit")
        type(KeyCode.DOWN)
        type(KeyCode.ENTER)
        then:
        view.model.item.profileManager.database.launch()
        view.model.item.profileManager.saveToDb(user)
        FxAssert.verifyThat(timeUnit, ComboBoxMatchers.hasItems(4))
        FxAssert.verifyThat(timeUnit, ComboBoxMatchers.hasSelectedItem(LoginDuration.Minutes))
        lookup(".user_timeout").query().getValue() == 1

    }

    void "Verify the viewmodel is accurate"() {
        given:
        def model = view.model

        expect:
        model.username.value == username
        model.password.value == password
        model.duration.value == 1
    }

    void "It clicks log in"() {
        expect:
        clickOn(".login_login")
    }

    void "Validates the user logged in"() {
        expect:
        view.model.userLogin.value as Either.Right
    }

}
