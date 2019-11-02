package itasser.app.mytasser.app.login

import arrow.core.None
import arrow.core.Some
import tornadofx.ItemViewModel

class LoginModalModel : ItemViewModel<LoginModalController>(LoginModalController()) {
    val username = bind(LoginModalController::usernameProperty)
    val password = bind(LoginModalController::passwordProperty)
    val duration = bind(LoginModalController::durationProperty)
    val timeUnit = bind(LoginModalController::timeUnitProperty)
    val userLogin = bind(LoginModalController::sessionProperty)
    val loginError = bind(LoginModalController::loginErrorProperty)

    val isLoggedIn
        get() = when (val login = userLogin.value) {
            is None -> false
            is Some -> item.profileManager.isLoggedIn(login.t)
        }

    fun login() = commit()
        .let { item.onLogin() }
}

