package itasserui.app.mytasser.installwizard


import itasser.app.mytasser.app.installwizard.InstallWizard
import itasserui.app.mytasser.UserAppSpec
import org.testfx.service.query.NodeQuery
import tornadofx.Scope

import java.nio.file.Files

abstract class InstallWizardSpec extends UserAppSpec<InstallWizard> {
    NodeQuery next_node = null
    NodeQuery finish_node = null
    NodeQuery back_node = null

    void setup() {
        next_node = lookup(".button").nth(2)
        finish_node = lookup(".button").nth(1)
        back_node = lookup(".button").nth(0)
        def file = pkg.resolve("run-ITASSER.pl")
        Files.createDirectories(pkg)
        Files.createFile(file)
        Files.createDirectories(datadir)
        Files.createDirectories(libdir)
        Files.createDirectories(javaHome)
    }

    @Override
    InstallWizard create() {
        setupStuff()
        return new InstallWizard(new Scope())
    }
}
