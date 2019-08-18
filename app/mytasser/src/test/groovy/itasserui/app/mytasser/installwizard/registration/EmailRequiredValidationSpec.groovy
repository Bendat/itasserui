package itasserui.app.mytasser.installwizard.registration


import itasserui.app.mytasser.installwizard.InstallWizardSpec
import javafx.scene.control.Button
import org.testfx.api.FxAssert
import org.testfx.matcher.base.NodeMatchers
import spock.lang.Shared

class EmailRequiredValidationSpec extends InstallWizardSpec {
    void "Fills the wizard form with valid values except for email"() {
        given:
        def password = fake.internet().password(8, 10, true, true)
        password += "A}"
        next = lookup(".button").nth(2).queryButton()

        when:
        clickOn(".name").write(fake.name().username())
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)

        then:
        FxAssert.verifyThat(next, NodeMatchers.isDisabled())
    }

    void "Enters an empty email"(){
        when:
        clickOn(".email").write("")

        then:
        FxAssert.verifyThat(next, NodeMatchers.isDisabled())
    }

    void "Enters an invalid username and verifies we cannot proceed"() {
        when:
        clickOn(".email").write(fake.name().username())

        then:
        FxAssert.verifyThat(next, NodeMatchers.isDisabled())
    }

    void "Enters a valid email and verifies we can proceed"() {
        when:
        clickOn(".email").write(fake.internet().emailAddress())

        then:
        FxAssert.verifyThat(next, NodeMatchers.isEnabled())
    }
}
