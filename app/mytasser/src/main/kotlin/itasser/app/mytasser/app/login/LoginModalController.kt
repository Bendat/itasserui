package itasser.app.mytasser.app.login

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import itasser.app.mytasser.app.login.LoginModal.LoginDuration
import itasser.app.mytasser.app.login.LoginModal.LoginDuration.*
import itasserui.app.mytasser.lib.extensions.bind
import itasserui.app.mytasser.lib.kInject
import itasserui.app.user.ProfileManager
import itasserui.app.user.ProfileManager.Profile
import itasserui.common.`typealias`.Outcome
import itasserui.common.errors.RuntimeError
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.logger.Logger
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue
import java.time.Duration

@Suppress("MemberVisibilityCanBePrivate", "unused")
class LoginModalController : Controller(), Logger {

    val profileManager: ProfileManager by kInject()
    val usersProperty = FXCollections.observableArrayList<String>()
        .bind(profileManager.profiles) { it.user.username.value }
    val usernameProperty = SimpleObjectProperty("")
    val username: String by usernameProperty

    val passwordProperty = SimpleObjectProperty("")
    val password: String by passwordProperty

    val timeUnitProperty = SimpleObjectProperty(Seconds)
    val timeUnit: LoginDuration by timeUnitProperty

    val durationProperty = SimpleObjectProperty<Int>()
    val duration: Int by durationProperty

    val loginErrorProperty = SimpleObjectProperty<Option<RuntimeError>>(None)
    var loginError: Option<RuntimeError> by loginErrorProperty


    fun onLogin(): Outcome<Profile> =
        profileManager.login(
            username = Username(username),
            password = RawPassword(password),
            duration = durationStringConverter(duration)
        ).mapLeft {
            it.also { err ->
                loginError = Some(err)
            }
        }


    fun durationStringConverter(string: Int): Duration =
        when (timeUnit) {
            Minutes -> Duration.ofMinutes(string.toLong())
            Hours -> Duration.ofHours(string.toLong())
            Seconds -> Duration.ofSeconds(string.toLong())
        }
}