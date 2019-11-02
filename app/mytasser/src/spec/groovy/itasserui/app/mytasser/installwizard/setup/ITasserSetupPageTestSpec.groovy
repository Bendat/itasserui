package itasserui.app.mytasser.installwizard.setup


import itasserui.app.mytasser.installwizard.InstallWizardSpec
import org.testfx.api.FxAssert
import org.testfx.api.FxToolkit
import org.testfx.matcher.base.NodeMatchers


class ITasserSetupPageTestSpec extends InstallWizardSetupSpec {

    void "Should fail to proceed due to bad pkgdir"() {
        when:
        clickOn(".pkgdir").write("/badpackage")
        clickOn(".datadir").write(datadir.toString())
        clickOn(".libdir").write(libdir.toString())
        clickOn(".java_home ").write(javaHome.toString())

        then:
        FxAssert.verifyThat(finish_node, NodeMatchers.isDisabled())
        clickOn(finish_node.queryButton())
    }


    void "Should fail to proceed due to bad libdir"() {
        when:
        clickOn(".pkgdir").write(pkg.toString())
        clickOn(".libdir").write("/badpackage")
        clickOn(".datadir").write(datadir.toString())
        clickOn(".java_home ").write(javaHome.toString())

        then:
        FxAssert.verifyThat(finish_node, NodeMatchers.isDisabled())
    }


    void "Should fail to proceed due to bad datadir"() {
        when:
        clickOn(".datadir").write("/badpackage")
        clickOn(".libdir").write(libdir.toString())

        then:
        FxAssert.verifyThat(finish_node, NodeMatchers.isDisabled())
    }


    void "Should fail to proceed due to bad java_home"() {
        when:
        clickOn(".datadir").write(datadir.toString())
        clickOn(".java_home ").write("/badpackage")
        clickOn(".pkgdir").write("/badpackage")
        clickOn(".libdir").write(libdir.toString())

        then:
        FxAssert.verifyThat(finish_node, NodeMatchers.isDisabled())
    }

    void "Should fail to proceed due to lack of itasser script"() {
        when:
        clickOn(".pkgdir").write("/badpackage/runI-TASSER.pl")
        clickOn(".java_home ").write(javaHome.toString())
        clickOn(".datadir").write(datadir.toString())
        clickOn(".libdir").write(libdir.toString())

        then:
        FxAssert.verifyThat(finish_node, NodeMatchers.isDisabled())
    }


    void "Should validate to progression"() {
        given:
        view.model.item.databasePath = tmpdirPath

        when:
        clickOn(".pkgdir").write(pkg.toString())
        clickOn(".datadir").write(datadir.toString())
        clickOn(".libdir").write(libdir.toString())
        clickOn(".java_home ").write(javaHome.toString())

        then:
        FxAssert.verifyThat(finish_node  , NodeMatchers.isEnabled())
        clickOn(finish_node.queryButton())

    }
}