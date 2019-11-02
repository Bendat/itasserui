package itasserui.app.mytasser.installwizard.setup


import org.testfx.api.FxToolkit

import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.base.NodeMatchers.isDisabled
import static org.testfx.matcher.base.NodeMatchers.isEnabled

class ParamValidationSpec extends InstallWizardSetupSpec {

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
        when:
        clickOn(pkgdirNode.query()).write(datadir.toString())
        clickOn(libDirNode.query()).write(libdir.toString())
        clickOn(dataDirNode.query()).write(datadir.toString())
        clickOn(javaHomeNode.query()).write(javaHome.toString())

        then:
        verifyThat(finish_node, isDisabled())
    }

    void "Other directories must exist"() {
        when:
        clickOn(pkgdirNode.query()).write(pkg.toString())
        clickOn(dataDirNode.query()).write(uncreated.toAbsolutePath().toString())
        clickOn(libDirNode.query()).write(libdir.toString())
        clickOn(javaHomeNode.query()).write(javaHome.toString())

        then:
        verifyThat(finish_node, isDisabled())
    }
}
