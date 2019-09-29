package itasser.app.mytasser.app.login

import arrow.core.Try
import itasser.app.mytasser.app.login.LoginModal.LoginDuration
import itasser.app.mytasser.app.login.LoginModal.LoginDuration.*
import itasser.app.mytasser.lib.DI
import itasserui.app.user.ProfileManager
import itasserui.app.user.ProfileManager.Session
import itasserui.common.`typealias`.Outcome
import itasserui.common.extensions.mapLeft
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Alert.AlertType.ERROR
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.conf.global
import org.kodein.di.generic.instance
import tornadofx.*
import java.time.Duration


class LoginModal(
    titleText: String = "User Login"
) : View() {
    val model: LoginModalModel by inject()

    fun <T : Node> T.id(fxId: String): T {
        id = fxId
        return this
    }

    @Suppress("unused")
    enum class LoginDuration {
        Actions,
        Seconds,
        Minutes,
        Hours
    }

    override val root: Parent = hbox {
        spacer()
        form {
            maxWidth = 520.0
            maxHeight = 250.0
            fieldset(titleText) {
                field("Username") { textfield(model.username).id("username_field") }
                field("Password") { passwordfield(model.password) { }.id("password_field") }
                field("Log in for:") {
                    hbox {
                        val values = values().toList()
                        spinner(-1, 100000, 0, 1, false, model.duration, true) { maxWidth = 80.0 }.id("user_timeout")
                        combobox(model.timeUnit, values) { value = Actions }.id("timeout_unit")
                    }

                }
            }
            hbox {
                spacer()
                button("Cancel") { setOnMouseClicked { currentStage?.close() } }.id("login_cancel")
                spacer()
                button("Login") { setOnMouseClicked { model.login(); currentStage?.close() } }.id("login_login")
            }
        }
        spacer()

    }

}


@Suppress("MemberVisibilityCanBePrivate")
class LoginModalController : Controller(), KodeinAware {
    val kdi: DI by inject<DI>()
    override val kodein: Kodein
        get() = kdi.kodein
    val profileManager: ProfileManager by instance()
    val usernameProperty = SimpleObjectProperty<String>()
    val username: String by usernameProperty

    val passwordProperty = SimpleObjectProperty<String>()
    val password: String by passwordProperty

    val timeUnitProperty = SimpleObjectProperty<LoginDuration>()
    val timeUnit: LoginDuration by timeUnitProperty

    val durationProperty = SimpleObjectProperty<Int>()
    val duration: Int by durationProperty

    val sessionProperty = SimpleObjectProperty<Outcome<Session>>()
    var session: Outcome<Session> by sessionProperty

    fun onLogin() =
        Try {
            profileManager.login(
                username = Username(username),
                password = RawPassword(password),
                duration = durationStringConverter(duration)
            ).also { session = it }
        }.mapLeft {
            alert(
                ERROR,
                "Couldn't log in",
                it.message,
                owner = this.primaryStage,
                title = "Couldn't log in"
            )
        }


    fun durationStringConverter(string: Int): Duration =
        when (timeUnit) {
            Minutes -> Duration.ofMinutes(string.toLong())
            Hours -> Duration.ofHours(string.toLong())
            Seconds -> Duration.ofSeconds(string.toLong())
            else -> Duration.ofMillis(string.toLong())
        }


}

class LoginModalModel : ItemViewModel<LoginModalController>(LoginModalController()) {

    val username = bind(LoginModalController::usernameProperty)
    val password = bind(LoginModalController::passwordProperty)
    val duration = bind(LoginModalController::durationProperty)
    val timeUnit = bind(LoginModalController::timeUnitProperty)
    val userLogin = bind(LoginModalController::sessionProperty)
    fun login() = commit()
        .also { item.onLogin() }
}


