package itasserui.app.mytasser.installwizard.registration

import itasserui.app.mytasser.installwizard.InstallWizardSpec
import org.testfx.api.FxAssert
import org.testfx.api.FxToolkit
import org.testfx.matcher.base.NodeMatchers

import static org.testfx.api.FxAssert.verifyThat

class PasswordValidationSpec extends InstallWizardSpec {
    void setup() {
        when:
        clickOn(".name").write(fake.name().username())
        clickOn(".email").write(email)

        then:
        verifyThat(next_node, NodeMatchers.isDisabled())
    }

    void "Can proceed with valid password"() {
        when:
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)

        then:
        verifyThat(next_node, NodeMatchers.isEnabled())
    }

    void "Should not proceed with mismatched repeat password"() {
        when:
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(fake.internet().password())

        then:
        verifyThat(next_node, NodeMatchers.isDisabled())
    }

    void "Should not proceed with empty passwords"() {
        expect:
        verifyThat(next_node, NodeMatchers.isDisabled())
    }

    void "Should not proceed with invalid password"() {
        when:
        clickOn(".password").write("abcd")
        clickOn(".password-repeat").write("abcd")

        then:
        verifyThat(next_node, NodeMatchers.isDisabled())
    }
}
