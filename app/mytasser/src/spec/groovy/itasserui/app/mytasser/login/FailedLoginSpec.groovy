package itasserui.app.mytasser.login

import arrow.core.Some
import itasserui.common.errors.RuntimeError
import org.testfx.service.query.NodeQuery

import static itasser.app.mytasser.app.login.LoginModalCss.INSTANCE as css

class FailedLoginSpec extends LoginSpec {
    NodeQuery timeoutTime = null

    void setup() {
        timeoutTime = lookup(css.loginTimeoutSpinner.render())
    }

    void "Invalid username"() {
        when: "An unknown username is entered"
        clickOn(css.loginUsernameField.render()).write("badAdmin")
        clickOn(css.loginPasswordField.render()).write(password)

        then: "Attempt to login"
        clickOn(css.loginLoginButton.render())

        and: "Verify the error modal is displayed"
        def error = view.model.loginError.value as Some<RuntimeError>
        error.t.errorType == "NoSuchUser"
        alertHeader() == "No Such User"
        alertContent().contains("User: Username(value=badAdmin)")

        and: "Close the modal"
        clickOn("OK")
    }

    void "Invalid password"() {
        when: "An unknown username is entered"
        clickOn(css.loginUsernameField.render()).write(user.username.value)
        clickOn(css.loginPasswordField.render()).write("123}}")

        then: "Attempt to login"
        clickOn(css.loginLoginButton.render())

        and: "Verify the error modal is displayed"
        def error = view.model.loginError.value as Some<RuntimeError>
        alertHeader() == "Wrong Password"
        alertContent().contains(error.t.toString())

    }

}
