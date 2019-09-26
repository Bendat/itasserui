package itasserui.app.mytasser.installwizard.setup


import itasserui.app.mytasser.installwizard.InstallWizardSpec
import org.testfx.api.FxAssert
import org.testfx.api.FxToolkit
import org.testfx.matcher.base.NodeMatchers


class ITasserSetupPageTestSpec extends InstallWizardSpec {

    private void "Proceed to ITasser setup page"() {
        when:
        clickOn(".name").write(username)
        clickOn(".email").write(email)
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)
        clickOn(next_node.queryButton())

        then:
        FxAssert.verifyThat(finish_node, NodeMatchers.isDisabled())
        FxAssert.verifyThat(back_node, NodeMatchers.isEnabled())

    }

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
        given:
        interact{
            lookup(".pkgdir").queryTextInputControl().clear()
            lookup(".libdir").queryTextInputControl().clear()
        }

        when:
        clickOn(".pkgdir").write(pkg.toString())
        clickOn(".libdir").write("/badpackage")

        then:
        FxAssert.verifyThat(finish_node, NodeMatchers.isDisabled())
    }


    void "Should fail to proceed due to bad datadir"() {
        given:
        interact{
            lookup(".datadir").queryTextInputControl().clear()
            lookup(".libdir").queryTextInputControl().clear()
        }

        when:
        clickOn(".datadir").write("/badpackage")
        clickOn(".libdir").write(libdir.toString())

        then:
        FxAssert.verifyThat(finish_node, NodeMatchers.isDisabled())
    }


    void "Should fail to proceed due to bad java_home"() {
        given:
        interact{
            lookup(".java_home").queryTextInputControl().clear()
            lookup(".datadir").queryTextInputControl().clear()
        }

        when:
        clickOn(".datadir").write(datadir.toString())
        clickOn(".java_home ").write("/badpackage")

        then:
        FxAssert.verifyThat(finish_node, NodeMatchers.isDisabled())
    }

    void "Should fail to proceed due to lack of itasser script"() {
        given:
        interact{
            lookup(".java_home").queryTextInputControl().clear()
            lookup(".pkgdir").queryTextInputControl().clear()
        }


        when:
        clickOn(".pkgdir").write("/badpackage/runI-TASSER.pl")
        clickOn(".java_home ").write(javaHome.toString())

        then:
        FxAssert.verifyThat(finish_node, NodeMatchers.isDisabled())
    }


    void "Should validate to progression"() {
        given:
        interact{
            lookup(".pkgdir").queryTextInputControl().clear()
        }

        when:
        clickOn(".pkgdir").write(pkg.toString())

        then:
        FxAssert.verifyThat(finish_node  , NodeMatchers.isEnabled())
        clickOn(finish_node.queryButton())
        FxToolkit.hideStage()

    }



}