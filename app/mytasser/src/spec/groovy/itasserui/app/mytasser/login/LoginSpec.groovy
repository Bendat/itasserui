package itasserui.app.mytasser.login

import itasser.app.mytasser.app.login.LoginModal
import itasser.app.mytasser.kodeinmodules.DependencyInjector
import itasserui.app.mytasser.AppSpec
import itasserui.app.mytasser.lib.DI
import itasserui.app.mytasser.lib.ITasserSettings
import itasserui.app.user.UnregisteredUser
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import org.testfx.api.FxAssert
import org.testfx.api.FxToolkit
import org.testfx.matcher.control.ComboBoxMatchers
import org.testfx.service.query.NodeQuery
import tornadofx.Scope

import java.nio.file.Path

abstract class LoginSpec extends AppSpec<LoginModal> {
    NodeQuery timeUnit = null

    private ITasserSettings settins = new ITasserSettings(tmpdirPath, libdir, javaHome, datadir, "gnuparallel", UUID.randomUUID())

    void cleanup() {
        FxToolkit.hideStage()
    }

    void setup() {
        setup:
        def user = new UnregisteredUser(new Username(username), new RawPassword(password), new EmailAddress(email), false).toUser(UUID.randomUUID())
        timeUnit = lookup("#timeout_unit")

        expect:
        "The combobox to have ${LoginModal.LoginDuration.values().size()}"
        FxAssert.verifyThat(timeUnit, ComboBoxMatchers.hasItems(3))
        FxAssert.verifyThat(timeUnit, ComboBoxMatchers.hasSelectedItem(LoginModal.LoginDuration.Seconds))

        and:
        "A new user $username to exist in the database"
        view.model.item.profileManager.database.launch()
        view.model.item.profileManager.createUserProfile(user)
    }

    @Override
    LoginModal create() {
        println("Dir is ${tmpdirPath.toAbsolutePath()}")
        def path = tmpdirPath as Path
        def kodein = DependencyInjector.INSTANCE.initializeKodein(
                username,
                password,
                settins,
                path)
        def view = new LoginModal("User Login", new Scope())
        view.setInScope(new DI(kodein), view.scope)
        return view
    }
}
