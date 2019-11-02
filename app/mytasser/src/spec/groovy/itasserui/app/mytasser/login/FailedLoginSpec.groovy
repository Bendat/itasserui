package itasserui.app.mytasser.login

import arrow.core.Some
import itasserui.common.errors.RuntimeError
import org.testfx.service.query.NodeQuery

class FailedLoginSpec extends LoginSpec {
    NodeQuery timeoutTime = null

    void setup() {
        timeoutTime = lookup("#user_timeout")
    }

    void "Invalid username"() {
        when: "An unknown username is entered"
        clickOn("#username_field").write("badAdmin")
        clickOn("#password_field").write(password)

        then: "Attempt to login"
        clickOn("#login_login")

        and: "Verify the error modal is displayed"
        def error = view.model.loginError.value as Some<RuntimeError>
        error.t.errorType == "NoSuchUser"
        alertHeader() == "NoSuchUser"
        alertContent().substring(0, 50) == error.t.toString().substring(0, 50)

        and: "Close the modal"
//        clickOn(".button")
    }

    void "Invalid password"() {
        when: "An unknown username is entered"
        clickOn("#username_field").write(username)
        clickOn("#password_field").write("123}}")

        then: "Attempt to login"
        clickOn("#login_login")

        and: "Verify the error modal is displayed"
        def error = view.model.loginError.value as Some<RuntimeError>
        alertHeader() == "WrongPassword"
        alertContent().substring(0, 50) == error.t.toString().substring(0, 50)
    }

}
