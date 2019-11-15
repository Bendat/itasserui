package itasserui.app.mytasser.installwizard.e2e

import arrow.core.None
import itasserui.app.mytasser.installwizard.InstallWizardSpec
import itasser.app.mytasser.lib.DI
import org.kodein.di.Kodein
import org.testfx.api.FxAssert
import org.testfx.matcher.base.NodeMatchers

import java.nio.file.Files

class InstallWizardE2ESpec extends InstallWizardSpec {

    void "Successful E2E test for the install wizard"() {
        given:
        view.model.databasePath.value = tmpdirPath

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

        and: "The generated Kodein is set in scope"
        def di = view.model.kodeinModule.value as arrow.core.Some<Kodein>
        view.setInScope(new DI(view.scope, di.t), view.scope)

        then: "Verify the viewmodel data is accurate"
        view.model.name.value == username
        view.controller.name == view.model.name.value
        view.controller.initStatus as None
        FxAssert.verifyThat(finish_node, NodeMatchers.isEnabled())

        and: "Given the database file"
        def file = tmpdirPath.resolve("database.idb")

        then: "Verify controller data is accurate"
        view.model.name.value == username
        view.controller.name == view.model.name.value
        view.controller.initStatus as None
        Files.exists(file)
        Files.size(file) > 0

        and: "Verify the settings have been stored"
        print("Settings are ${view.di}")
        view.settings.itasser.dataDir.normalize() == tmpdirPath.resolve("datadir").normalize()
        view.settings.itasser.runStyle == "gnuparallel"
    }

}
