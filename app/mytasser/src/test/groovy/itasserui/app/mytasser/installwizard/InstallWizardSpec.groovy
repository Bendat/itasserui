package itasserui.app.mytasser.installwizard

import arrow.core.Either
import itasser.app.mytasser.app.installwizard.InstallWizard
import itasserui.app.mytasser.AppSpec
import itasserui.lib.filemanager.FS
import org.testfx.api.FxAssert
import org.testfx.matcher.base.NodeMatchers
import org.testfx.service.query.NodeQuery
import spock.lang.Shared
import tornadofx.Scope

import java.nio.file.Path

abstract class InstallWizardSpec extends AppSpec<InstallWizard> {
    @Shared NodeQuery next_node = null
    @Shared NodeQuery finish_node = null
    @Shared NodeQuery back_node = null

    @Shared Path pkg = tmpdirPath.resolve("runI-TASSER.pl").toAbsolutePath()
    @Shared Path datadir = tmpdirPath.resolve("datadir").toAbsolutePath()
    @Shared Path libdir = tmpdirPath.resolve("lib").toAbsolutePath()
    @Shared Path javaHome = tmpdirPath.resolve("jdk").toAbsolutePath()

    @Override InstallWizard create() {
        return new InstallWizard(new Scope())
    }

    void "Setup: Gather the next button"() {
        given:
        next_node = lookup(".button").nth(2)
        finish_node = lookup(".button").nth(1)
        back_node = lookup(".button").nth(0)

        expect:
        FxAssert.verifyThat(next_node, NodeMatchers.isDisabled())
    }

    void "Setup: Create Test Directories"() {
        when:
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
