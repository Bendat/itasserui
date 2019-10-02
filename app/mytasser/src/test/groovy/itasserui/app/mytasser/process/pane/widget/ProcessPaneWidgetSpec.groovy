package itasserui.app.mytasser.process.pane.widget

import arrow.core.Either
import arrow.data.Ior
import arrow.data.NonEmptyList
import itasser.app.mytasser.app.process.pane.widget.ProcessWidget
import itasser.app.mytasser.kodeinmodules.DependencyInjector
import itasser.app.mytasser.lib.DI
import itasser.app.mytasser.lib.ITasserSettings
import itasserui.app.mytasser.AppSpec
import itasserui.app.user.Account
import itasserui.app.user.UnregisteredUser
import itasserui.app.user.User
import itasserui.common.errors.RuntimeError
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.utils.SafeWaitKt
import itasserui.lib.process.details.ExecutionState
import itasserui.lib.process.manager.ProcessManager
import itasserui.lib.process.process.ITasser
import javafx.scene.Scene
import javafx.stage.Stage
import org.kodein.di.Kodein
import tornadofx.Scope

import java.nio.file.Path
import java.nio.file.Paths

import static itasserui.common.utils.FakeKt.Fake

abstract class ProcessPaneWidgetSpec extends AppSpec<ProcessWidget> {
    private ITasserSettings settins = new ITasserSettings(tmpdirPath, libdir, javaHome, datadir, "gnuparallel", UUID.randomUUID())
    ProcessManager pm = new ProcessManager()
    Account account = new UnregisteredUser(
            new Username(Fake.name().username()),
            new RawPassword(Fake.internet().password()),
            new EmailAddress(Fake.internet().emailAddress()),
            false
    )
    User user = null
    Path dataDir = null
    ITasser itasser = null
    Kodein kodein = null

    void setupStuff() {
        def path = tmpdirPath as Path
        kodein = DependencyInjector.INSTANCE.initializeKodein(
                username,
                password,
                settins,
                path)
        def extractor = new KodeinExtractor(kodein)
        extractor.db.launch()

        def res = extractor.pm.createUserProfile(account) as Ior.Right<NonEmptyList<RuntimeError>, User>
        user = res.value
        extractor.pm.getUserDir(user, User.UserCategory.DataDir)
                .map { dataDir = it.toAbsolutePath() }
        itasser = extractor.proc.new(
                UUID.randomUUID(),
                0,
                Paths.get(""),
                "Automationyl Aminodril",
                new ArrayList<String>(),
                user.id,
                dataDir
        )
    }

    @Override
    void start(Stage stage) {
        setupStuff()
        this.view = create()
        def scene = new Scene(view.root)
        stage.setScene(scene)
        stage.show()
        this.stage = stage
    }

    @Override
    ProcessWidget create() {
        println("Dir is ${tmpdirPath.toAbsolutePath()}")
        def view = new ProcessWidget(user, itasser, new Scope())
        view.setInScope(new DI(view.scope, kodein), view.scope)
        return view
    }
}

class ProcSpec extends ProcessPaneWidgetSpec {
    void "Yo"(){
        given:
        SafeWaitKt.safeWait(5000)
        when:
        def a = 2
        then:
        1 == 1
    }
}