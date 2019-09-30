package itasser.app.mytasser.app.login

import tornadofx.ItemViewModel

class LoginModalModel : ItemViewModel<LoginModalController>(LoginModalController()) {
    val username = bind(LoginModalController::usernameProperty)
    val password = bind(LoginModalController::passwordProperty)
    val duration = bind(LoginModalController::durationProperty)
    val timeUnit = bind(LoginModalController::timeUnitProperty)
    val userLogin = bind(LoginModalController::sessionProperty)
    val loginError = bind(LoginModalController::loginErrorProperty)
    fun login() = commit()
        .let { item.onLogin() }
}

