package itasserui.app.mytasser.installwizard.setup

import itasserui.app.mytasser.installwizard.InstallWizardSpec
import org.testfx.service.query.NodeQuery
import spock.lang.Shared

import java.nio.file.Files
import java.nio.file.Path

import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.base.NodeMatchers.isDisabled
import static org.testfx.matcher.base.NodeMatchers.isEnabled

class ParamValidationSpec extends InstallWizardSpec {
    @Shared
    Path root = Files.createTempDirectory("guitest")
    @Shared
    Path pkgdirPath = root.resolve("pkgdir")
    @Shared
    Path itasser = pkgdirPath.resolve("runI-TASSER.pl")
    @Shared
    Path uncreated = root.resolve("uncreated")

    @Shared
    NodeQuery pkgdir = null
    @Shared
    NodeQuery datadir = null
    @Shared
    NodeQuery libdir = null
    @Shared
    NodeQuery javaHome = null

    void "Proceed to setup page"() {
        given:
        def password = fake.internet().password() + "S}"

        when:
        Files.createDirectories(pkgdirPath)
        Files.createFile(itasser)
        clickOn(".name").write(fake.name().username())
        clickOn(".email").write(fake.internet().emailAddress())
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)
        clickOn(next.query())

        then:
        verifyThat(finish, isDisabled())
    }


    void "Acquiring parameter fields"() {
        given:
        pkgdir = lookup(".pkgdir")
        datadir = lookup(".datadir")
        libdir = lookup(".libdir")
        javaHome = lookup('.java_home')
    }

    void "Valid paths should allow wizard completion"() {
        when:
        clickOn(pkgdir.query()).write(pkgdirPath.toAbsolutePath().toString())
        clickOn(libdir.query()).write(root.toAbsolutePath().toString())
        clickOn(datadir.query()).write(root.toAbsolutePath().toString())
        clickOn(javaHome.query()).write(root.toAbsolutePath().toString())

        then:
        verifyThat(finish, isEnabled())
    }

    void "Pkgdir must contain runI-TASSER.pl"() {
        given:
        interact { pkgdir.queryTextInputControl().clear() }

        when:
        clickOn(pkgdir.query()).write(root.toAbsolutePath().toString())

        then:
        verifyThat(finish, isDisabled())
    }

    void "Other directories must exist"() {
        given:
        interact {
            pkgdir.queryTextInputControl().clear()
            datadir.queryTextInputControl().clear()
        }

        when:
        clickOn(pkgdir.query()).write(pkgdirPath.toAbsolutePath().toString())
        clickOn(datadir.query()).write(uncreated.toAbsolutePath().toString())

        then:
        verifyThat(finish, isDisabled())
    }
}
