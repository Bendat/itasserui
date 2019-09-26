package itasserui.app.mytasser.installwizard.e2e


import arrow.core.None
import itasserui.app.mytasser.installwizard.InstallWizardSpec
import org.testfx.api.FxAssert
import org.testfx.matcher.base.NodeMatchers

import java.nio.file.Files

class InstallWizardE2ESpec extends InstallWizardSpec {

    void "Create the admin user"() {
        when:
        clickOn(".name").write(username)
        clickOn(".email").write(email)
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)

        then:
        FxAssert.verifyThat(next_node, NodeMatchers.isEnabled())
    }

    void "Set up global directories directories and files"() {
        given:
        def finish = finish_node.queryButton()
        when:

        clickOn(next_node.queryButton())
        clickOn(".pkgdir").write(pkg.toString())
        clickOn(".datadir").write(datadir.toString())
        clickOn(".libdir").write(libdir.toString())
        clickOn(".java_home ").write(javaHome.toString())

        then:
        view.model.name.value == username
        view.controller.name == view.model.name.value
        view.controller.initStatus as None
        FxAssert.verifyThat(finish_node, NodeMatchers.isEnabled())
        clickOn(finish)
    }

    void "Verify the viewmodel persists"() {
        given:
        def model = view.model
        def controller = view.controller

        expect:
        model.name.value == username
        controller.name == view.model.name.value
        controller.initStatus as None
        controller.isInitialized
    }

    void "Verify the database was created"() {
        given:
        def file = tmpdirPath.resolve("database.idb")

        expect:
        Files.exists(file)
        Files.size(file) > 0
    }

    void "Verify the created settings are accurate"() {
        expect:
        view.settings.itasser.dataDir.normalize() == tmpdirPath.resolve("datadir").normalize()
        view.settings.itasser.runStyle == "gnuparallel"
    }
}
