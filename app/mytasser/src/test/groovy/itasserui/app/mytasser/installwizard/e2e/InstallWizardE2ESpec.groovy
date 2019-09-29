package itasserui.app.mytasser.installwizard.e2e

import arrow.core.None
import itasserui.app.mytasser.installwizard.InstallWizardSpec
import org.testfx.api.FxAssert
import org.testfx.matcher.base.NodeMatchers

import java.nio.file.Files
import java.nio.file.Path

class InstallWizardE2ESpec extends InstallWizardSpec {
    @Override
    Path generateTestDir() {
        return Files.createTempDirectory("new1").toAbsolutePath()
    }

    void "Set up global directories directories and files"() {
        when: "New user is set up"
        clickOn(".name").write(username)
        clickOn(".email").write(email)
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)
        and: "Setting the databse path"
        view.model.item.databasePath = tmpdirPath

        then: "The wizard should allow user to proceed"
        FxAssert.verifyThat(next_node, NodeMatchers.isEnabled())

        when: "Proceeded to the ITasser setup page"
        clickOn(next_node.queryButton())
        clickOn(".pkgdir").write(pkg.toString())
        clickOn(".datadir").write(datadir.toString())
        clickOn(".libdir").write(libdir.toString())
        clickOn(".java_home ").write(javaHome.toString())
        clickOn(finish_node.queryButton())

        then: "Verify the viewmodel data is accurate"
        view.model.name.value == username
        view.controller.name == view.model.name.value
        view.controller.initStatus as None
        FxAssert.verifyThat(finish_node, NodeMatchers.isEnabled())

        and:
        def model = view.model
        def controller = view.controller
        def file = tmpdirPath.resolve("database.idb")


        then: "Verify controller data is accurate"
        model.name.value == username
        controller.name == view.model.name.value
        controller.initStatus as None
        controller.isInitialized
        Files.exists(file)
        Files.size(file) > 0

        and: "Verify the settings have been stored"
        view.settings.itasser.dataDir.normalize() == tmpdirPath.resolve("datadir").normalize()
        view.settings.itasser.runStyle == "gnuparallel"
    }

}
