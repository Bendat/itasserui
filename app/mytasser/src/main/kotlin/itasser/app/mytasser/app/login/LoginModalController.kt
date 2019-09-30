package itasser.app.mytasser.app.login

import arrow.core.*
import itasser.app.mytasser.app.login.LoginModal.LoginDuration
import itasser.app.mytasser.app.login.LoginModal.LoginDuration.*
import itasser.app.mytasser.lib.kInject
import itasserui.app.user.ProfileManager
import itasserui.app.user.ProfileManager.Session
import itasserui.common.`typealias`.Outcome
import itasserui.common.errors.ExceptionError
import itasserui.common.errors.RuntimeError
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.logger.Logger
import javafx.beans.property.SimpleObjectProperty
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue
import java.time.Duration

@Suppress("MemberVisibilityCanBePrivate")
class LoginModalController : Controller(), Logger {

    val profileManager: ProfileManager by kInject<ProfileManager>()

    val usernameProperty = SimpleObjectProperty("")
    val username: String by usernameProperty

    val passwordProperty = SimpleObjectProperty("")
    val password: String by passwordProperty

    val timeUnitProperty = SimpleObjectProperty(Actions)
    val timeUnit: LoginDuration by timeUnitProperty

    val durationProperty = SimpleObjectProperty<Int>()
    val duration: Int by durationProperty

    val sessionProperty = SimpleObjectProperty<Option<Session>>(None)
    var session: Option<Session> by sessionProperty

    val loginErrorProperty = SimpleObjectProperty<Option<RuntimeError>>(None)
    var loginError: Option<RuntimeError> by loginErrorProperty


    fun onLogin(): Outcome<Session> =
        Try {
            profileManager.login(
                username = Username(username),
                password = RawPassword(password),
                duration = durationStringConverter(duration)
            ).also { it.map { ses -> session = Some(ses) } }
        }.toEither {
            ExceptionError(it)
        }.flatMap {
            it
        }.mapLeft {
            it.also { err ->
                loginError = Some(err)
            }
            it
        }


    fun durationStringConverter(string: Int): Duration =
        when (timeUnit) {
            Minutes -> Duration.ofMinutes(string.toLong())
            Hours -> Duration.ofHours(string.toLong())
            Seconds -> Duration.ofSeconds(string.toLong())
            else -> Duration.ofMillis(string.toLong())
        }


}