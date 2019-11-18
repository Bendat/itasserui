package itasserui.app.mytasser.login

import itasser.app.mytasser.app.login.LoginModal
import itasserui.app.mytasser.UserAppSpec

abstract class LoginSpec extends UserAppSpec<LoginModal> {
    @Override
    LoginModal create() {
        setupStuff()
        return new LoginModal("User Login", testScope)
    }
}
