package itasserui.app.mytasser.installwizard

import itasser.app.mytasser.app.installwizard.InstallWizard
import itasserui.app.mytasser.AppSpec
import org.hamcrest.Matcher
import org.testfx.api.FxAssert
import org.testfx.matcher.base.NodeMatchers
import javafx.scene.Node
import org.testfx.service.query.NodeQuery
import spock.lang.Shared

abstract class InstallWizardSpec extends AppSpec<InstallWizard> {
    @Shared NodeQuery next = null
    @Shared NodeQuery finish = null
    @Override
    InstallWizard create() {
        return new InstallWizard()
    }

    void "Gather the next button"(){
        given:
        next = lookup(".button").nth(2)
        finish = lookup(".button").nth(1)
        expect:
        FxAssert.verifyThat(next, NodeMatchers.isDisabled())
    }
}
