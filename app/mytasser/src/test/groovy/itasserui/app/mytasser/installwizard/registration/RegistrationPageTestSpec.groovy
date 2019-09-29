package itasserui.app.mytasser.installwizard.registration

import itasser.app.mytasser.app.launcher.InstallWizardLauncher
import itasserui.app.mytasser.installwizard.InstallWizardSpec
import itasserui.common.utils.FakeKt
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.kodein.di.Kodein
import org.testfx.api.FxAssert
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec
import org.testfx.matcher.base.NodeMatchers
import static itasserui.common.utils.SafeWaitKt.safeWait

class RegistrationPageTestSpec extends InstallWizardSpec {

    void "should not proceed due to bad name"() {
        given:
        def password = password
        def next = lookup(".button").nth(2)

        when:
        clickOn(".name").write("")
        clickOn(".email").write(email)
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)

        then:
        FxAssert.verifyThat(next, NodeMatchers.isDisabled())
    }

    void "should not proceed due to bad email"() {

        when:
        clickOn(".name").write(username)
        clickOn(".email").write("bademail")
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)

        then:
        FxAssert.verifyThat(next_node, NodeMatchers.isDisabled())
    }

    void "should not proceed due to bad password"() {
        when:
        clickOn(".name").write(username)
        clickOn(".email").write(email)
        clickOn(".password").write("nogood")
        clickOn(".password-repeat").write(password)

        then:
        FxAssert.verifyThat(next_node, NodeMatchers.isDisabled())
    }

    void "should not proceed due to mismatched password"() {
        when:
        clickOn(".name").write(username)
        clickOn(".email").write(email)
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(fake.internet().password())

        then:
        FxAssert.verifyThat(next_node, NodeMatchers.isDisabled())
    }

    void "should validate to progression"() {

        when:
        clickOn(".name").write(username)
        clickOn(".email").write(email)
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)

        then:
        FxAssert.verifyThat(next_node, NodeMatchers.isEnabled())
    }

}
