package itasserui.app.mytasser.installwizard.setup

import itasserui.app.mytasser.installwizard.InstallWizardSpec
import org.testfx.service.query.NodeQuery
import spock.lang.Shared

import java.nio.file.Path

import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.base.NodeMatchers.isDisabled
import static org.testfx.matcher.base.NodeMatchers.isEnabled

class ParamValidationSpec extends InstallWizardSpec {

    @Shared
    Path uncreated = tmpdirPath.resolve("uncreated")

    @Shared
    NodeQuery pkgdirNode = null
    @Shared
    NodeQuery dataDirNode = null
    @Shared
    NodeQuery libDirNode = null
    @Shared
    NodeQuery javaHomeNode = null

    void "Proceed to setup page"() {
        given:
        def password = fake.internet().password() + "S}"

        when:
        clickOn(".name").write(fake.name().username())
        clickOn(".email").write(fake.internet().emailAddress())
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)
        clickOn(next_node.query())

        then:
        verifyThat(finish_node, isDisabled())
    }


    void "Acquiring textfield nodes"() {
        given:
        pkgdirNode = lookup(".pkgdir")
        dataDirNode = lookup(".datadir")
        libDirNode = lookup(".libdir")
        javaHomeNode = lookup('.java_home')
    }

    void "Valid paths should allow wizard completion"() {
        when:
        clickOn(pkgdirNode.query()).write(pkg.toString())
        clickOn(libDirNode.query()).write(libdir.toString())
        clickOn(dataDirNode.query()).write(datadir.toString())
        clickOn(javaHomeNode.query()).write(javaHome.toString())

        then:
        verifyThat(finish_node, isEnabled())
    }

    void "Pkgdir must contain runI-TASSER.pl"() {
        given:
        interact { pkgdirNode.queryTextInputControl().clear() }

        when:
        clickOn(pkgdirNode.query()).write(datadir.toString())

        then:
        verifyThat(finish_node, isDisabled())
    }

    void "Other directories must exist"() {
        given:
        interact {
            clearText(pkgdirNode)
            clearText(dataDirNode)
        }

        when:
        clickOn(pkgdirNode.query()).write(pkg.toString())
        clickOn(dataDirNode.query()).write(uncreated.toAbsolutePath().toString())

        then:
        verifyThat(finish_node, isDisabled())
    }
}
