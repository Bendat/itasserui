package itasserui.app.mytasser.login

import itasser.app.mytasser.app.login.LoginModal
import itasser.app.mytasser.kodeinmodules.DiInitializerKt
import itasser.app.mytasser.lib.ITasserSettings
import itasserui.app.mytasser.AppSpec
import spock.lang.Shared

import java.nio.file.Path

abstract class LoginTest extends AppSpec<LoginModal> {
    @Shared Path pkg = tmpdirPath.resolve("runI-TASSER.pl").toAbsolutePath()
    @Shared Path datadir = tmpdirPath.resolve("datadir").toAbsolutePath()
    @Shared Path libdir = tmpdirPath.resolve("lib").toAbsolutePath()
    @Shared Path javaHome = tmpdirPath.resolve("jdk").toAbsolutePath()
    @Shared private ITasserSettings settins = new ITasserSettings(tmpdirPath, libdir, javaHome, datadir, "gnuparallel", UUID.randomUUID())

    @Override
    LoginModal create() {
        DiInitializerKt.diInitializer(username, password, settins)
        return new LoginModal("User Login")
    }
}
