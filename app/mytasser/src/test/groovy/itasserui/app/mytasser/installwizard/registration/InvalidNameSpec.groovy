package itasserui.app.mytasser.installwizard.registration

import itasser.app.mytasser.app.installwizard.InstallWizard
import itasserui.app.mytasser.AppSpec
import itasserui.app.mytasser.installwizard.InstallWizardSpec
import org.testfx.api.FxAssert
import org.testfx.matcher.base.NodeMatchers

import static itasserui.common.utils.SafeWaitKt.safeWait

class InvalidNameSpec extends InstallWizardSpec{

    void "Verifies the wizard will not progress without a username"() {
        given:
        def password = fake.internet().password(8, 10, true, true)
        def next = lookup(".button").nth(2).queryButton()

        when:
        clickOn(".name").write("")
        clickOn(".email").write(fake.internet().emailAddress())
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)

        then:
        safeWait(100)
        FxAssert.verifyThat(next, NodeMatchers.isDisabled())
    }

}
