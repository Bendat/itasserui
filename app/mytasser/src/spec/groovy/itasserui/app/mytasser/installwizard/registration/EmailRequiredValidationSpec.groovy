package itasserui.app.mytasser.installwizard.registration


import itasserui.app.mytasser.installwizard.InstallWizardSpec
import org.testfx.api.FxAssert
import org.testfx.api.FxToolkit
import org.testfx.matcher.base.NodeMatchers

class EmailRequiredValidationSpec extends InstallWizardSpec {

    void setup(){
        clickOn(".name").write(fake.name().username())
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)
    }


    void "Enters an empty email and verifies we cannot proceed"() {
        when:
        clickOn(".email").write(fake.name().username())

        then:
        FxAssert.verifyThat(next_node, NodeMatchers.isDisabled())
    }

    void "Enters an invalid email address and verifies we cannot proceed"() {
        when:
        clickOn(".email").write(fake.name().username())

        then:
        FxAssert.verifyThat(next_node, NodeMatchers.isDisabled())
    }

    void "Enters a valid email and verifies we can proceed"() {
        when:
        clickOn(".email").write(fake.internet().emailAddress())

        then:
        FxAssert.verifyThat(next_node, NodeMatchers.isEnabled())
    }

}
