package itasserui.app.mytasser.installwizard

import itasser.app.mytasser.app.launcher.InstallWizardLauncher
import itasserui.common.utils.FakeKt
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.kodein.di.Kodein
import org.testfx.api.FxAssert
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec
import org.testfx.matcher.base.NodeMatchers

import static itasserui.common.utils.SafeWaitKt.safeWait

class RegistrationPageTestSpec extends ApplicationSpec {
    InstallWizardLauncher app = null
    Stage stage

    private def faker = FakeKt.getFake()

    @Override
    void start(Stage stage) {
        println("Stage is ${this.stage}")
        app = new InstallWizardLauncher()
        if (this.stage == null) {
            FxToolkit.registerPrimaryStage()
            FxToolkit.setupFixture {
                this.stage = new Stage(StageStyle.UNIFIED)
                app.start(stage)
                stage.show()
            }
        }
    }

    @Override
    void stop() throws Exception {
        FxToolkit.hideStage()
        app.stop()
    }

    void cleanup() {
        FxToolkit.hideStage()
        app.stop()
    }

    void "should not proceed due to bad name"() {
        given:
        def password = faker.internet().password(8, 10, true, true)
        def next = lookup(".button").nth(2).queryButton()

        when:
        clickOn(".name").write("")
        clickOn(".email").write(faker.internet().emailAddress())
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)

        then:
        safeWait(500)
        FxAssert.verifyThat(next, NodeMatchers.isDisabled())
    }

    void "should not proceed due to bad email"() {
        given:
        def password = faker.internet().password(8, 10, true, true)
        def next = lookup(".button").nth(2).queryButton()

        when:
        clickOn(".name").write(faker.name().fullName())
        clickOn(".email").write("bademail")
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)

        then:
        safeWait(500)
        FxAssert.verifyThat(next, NodeMatchers.isDisabled())
    }

    void "should not proceed due to bad password"() {
        given:
        def password = faker.internet().password(8, 10, true, true)
        def next = lookup(".button").nth(2).queryButton()

        when:
        clickOn(".name").write(faker.name().fullName())
        clickOn(".email").write(faker.internet().emailAddress())
        clickOn(".password").write("nogood")
        clickOn(".password-repeat").write(password)

        then:
        safeWait(500)
        FxAssert.verifyThat(next, NodeMatchers.isDisabled())
    }

    void "should not proceed due to mismatched password"() {
        given:
        def password = faker.internet().password(8, 10, true, true)
        def next = lookup(".button").nth(2).queryButton()

        when:
        clickOn(".name").write(faker.name().fullName())
        clickOn(".email").write(faker.internet().emailAddress())
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(faker.internet().password())

        then:
        safeWait(500)
        FxAssert.verifyThat(next, NodeMatchers.isDisabled())
    }

    void "should validate to progression"() {
        given:
        def password = faker.internet()
                .password(8, 10, true, true) +"{"
        def next = lookup(".button").nth(2).queryButton()

        when:
        clickOn(".name").write(faker.name().fullName())
        clickOn(".email").write(faker.internet().emailAddress())
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)

        then:
        safeWait(500)
        FxAssert.verifyThat(next, NodeMatchers.isEnabled())
    }

}
