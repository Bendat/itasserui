package itasserui.app.mytasser.installwizard

import arrow.core.Either
import itasser.app.mytasser.app.installwizard.InstallWizard
import itasserui.app.mytasser.AppSpec
import itasserui.lib.filemanager.FS
import org.testfx.api.FxAssert
import org.testfx.matcher.base.NodeMatchers
import org.testfx.service.query.NodeQuery
import tornadofx.Scope


abstract class InstallWizardSpec extends AppSpec<InstallWizard> {
    NodeQuery next_node = null
    NodeQuery finish_node = null
    NodeQuery back_node = null

    @Override InstallWizard create() {
        def wiz = new InstallWizard(new Scope())
        return wiz
    }

    void setup(){
        given:
        next_node = lookup(".button").nth(2)
        finish_node = lookup(".button").nth(1)
        back_node = lookup(".button").nth(0)

        then:
        FxAssert.verifyThat(next_node, NodeMatchers.isDisabled())
        def pkgCreated = FS.create.file(pkg)
        def dataCreated = FS.create.directories(datadir)
        def libCreated = FS.create.directories(libdir)
        def javaCreated = FS.create.directories(javaHome)

        then:
        pkgCreated instanceof Either.Right
        dataCreated instanceof Either.Right
        libCreated instanceof Either.Right
        javaCreated instanceof Either.Right
    }
}
