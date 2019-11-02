package itasserui.app.mytasser.installwizard.setup

import itasserui.app.mytasser.installwizard.InstallWizardSpec
import org.testfx.service.query.NodeQuery
import spock.lang.Shared

import java.nio.file.Path

import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.base.NodeMatchers.isDisabled

abstract class InstallWizardSetupSpec extends InstallWizardSpec {

    Path uncreated = tmpdirPath.resolve("uncreated")
    NodeQuery pkgdirNode = null
    NodeQuery dataDirNode = null
    NodeQuery libDirNode = null
    NodeQuery javaHomeNode = null
    void setup() {
        given:
        pkgdirNode = lookup(".pkgdir")
        dataDirNode = lookup(".datadir")
        libDirNode = lookup(".libdir")
        javaHomeNode = lookup('.java_home')

        when:
        clickOn(".name").write(fake.name().username())
        clickOn(".email").write(fake.internet().emailAddress())
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)
        clickOn(next_node.query())

        then:
        verifyThat(finish_node, isDisabled())
    }
}
