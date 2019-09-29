package itasserui.app.mytasser.login

import itasser.app.mytasser.app.login.LoginModal
import itasser.app.mytasser.kodeinmodules.DependencyInjector
import itasser.app.mytasser.lib.DI
import itasser.app.mytasser.lib.ITasserSettings
import itasserui.app.mytasser.AppSpec
import spock.lang.Shared
import tornadofx.Component

import java.nio.file.Path

abstract class LoginSpec extends AppSpec<LoginModal> {

    @Shared private ITasserSettings settins = new ITasserSettings(tmpdirPath, libdir, javaHome, datadir, "gnuparallel", UUID.randomUUID())


    @Override
    LoginModal create() {
        println("Dir is ${tmpdirPath.toAbsolutePath()}")
        def path = tmpdirPath as Path
        def kodein = DependencyInjector.INSTANCE.initializeKodein(
                username,
                password,
                settins,
                path)
        def view = new LoginModal("User Login")
        view.setInScope(new DI(view.scope, kodein), view.scope)
        return view
    }
}
