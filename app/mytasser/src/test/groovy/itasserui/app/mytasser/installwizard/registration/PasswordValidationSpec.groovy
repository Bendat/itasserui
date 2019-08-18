package itasserui.app.mytasser.installwizard.registration

import itasserui.app.mytasser.installwizard.InstallWizardSpec
import org.testfx.api.FxAssert
import org.testfx.matcher.base.NodeMatchers
import org.testfx.matcher.control.TextInputControlMatchers

import static org.testfx.api.FxAssert.*

class PasswordValidationSpec extends InstallWizardSpec {
    void "Fill untested fields with valid values"() {
        given:
        def name = fake.name().username()
        def textfield = lookup(".name")

        when:
        clickOn(".name").write(name)
        clickOn(".email").write(fake.internet().emailAddress())

        then:
        verifyThat(next, NodeMatchers.isDisabled())
        FxAssert.verifyThat(textfield, TextInputControlMatchers.hasText(name))
    }

    void "Can proceed wit valid password"() {
        given:
        def password = fake.internet().password() + "S}"

        when:
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)

        then:
        verifyThat(next, NodeMatchers.isEnabled())
    }

    void "Should not proceed with mismatched repeat password"() {
        when:
        clickOn(".password-repeat").write(fake.internet().password())

        then:
        verifyThat(next, NodeMatchers.isDisabled())
    }

    void "Should not proceed with empty passwords"() {
        when:
        clearText(".password")
        clearText(".password-repeat")

        then:
        verifyThat(next, NodeMatchers.isDisabled())
    }

    void "Should not proceed with invalid password"() {
        when:
        clickOn(".password").write("abcd")
        clickOn(".password-repeat").write("abcd")

        then:
        verifyThat(next, NodeMatchers.isDisabled())
    }
}
