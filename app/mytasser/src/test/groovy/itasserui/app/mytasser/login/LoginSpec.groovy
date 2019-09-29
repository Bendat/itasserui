package itasserui.app.mytasser.login

import itasser.app.mytasser.app.login.LoginModal
import itasser.app.mytasser.kodeinmodules.DependencyInjector
import itasser.app.mytasser.lib.ITasserSettings
import itasserui.app.mytasser.AppSpec
import spock.lang.Shared

import java.nio.file.Path

abstract class LoginSpec extends AppSpec<LoginModal> {

    @Shared private ITasserSettings settins = new ITasserSettings(tmpdirPath, libdir, javaHome, datadir, "gnuparallel", UUID.randomUUID())

    void setupSpec() {
       DependencyInjector.initializeKodein(username, password, settins)
    }

    @Override
    LoginModal create() {
        return new LoginModal("User Login")
    }
}
