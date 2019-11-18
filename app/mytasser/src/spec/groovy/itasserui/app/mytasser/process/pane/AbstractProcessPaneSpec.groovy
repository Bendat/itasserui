package itasserui.app.mytasser.process.pane

import arrow.core.Either
import arrow.data.Ior
import arrow.data.NonEmptyList
import itasser.app.mytasser.app.process.pane.ProcessPane
import itasser.app.mytasser.kodeinmodules.DependencyInjector
import itasserui.app.mytasser.AppSpec
import itasserui.app.mytasser.KodeinExtractor
import itasser.app.mytasser.lib.DI
import itasser.app.mytasser.lib.ITasserSettings
import itasserui.app.user.ProfileManager
import itasserui.app.user.UnregisteredUser
import itasserui.app.user.User
import itasserui.common.errors.RuntimeError
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.lib.filemanager.FS
import itasserui.lib.process.process.ITasser
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import org.kodein.di.Kodein
import tornadofx.Scope

import java.nio.file.Path
import java.time.Duration

import static itasserui.common.utils.FakeKt.Fake

class AbstractProcessPaneSpec extends AppSpec<ProcessPane> {
    private ITasserSettings settings = new ITasserSettings(tmpdirPath, libdir, javaHome, datadir, "gnuparallel", UUID.randomUUID())
    Path dataDir = null
    UnregisteredUser account = new UnregisteredUser(
            new Username(Fake.name().username()),
            new RawPassword(Fake.internet().password()),
            new EmailAddress(Fake.internet().emailAddress()),
            false)
    User user = null
    ITasser itasser = null
    Kodein kodein = null
    KodeinExtractor extractor = null
    void login(Boolean succeed = true) {
        clickOn("#username_field").write(account.username.value)
        if (succeed)
            clickOn("#password_field").write(account.password.value)
        else
            clickOn("#password_field").write("horp")

        for (int i = 0; i < 4; i++) {
            clickOn(".increment-arrow-button")
        }
        clickOn("#timeout_unit")
        type(KeyCode.DOWN)
        type(KeyCode.ENTER)
        clickOn("#login_login")
    }

    void setupStuff() {
        println("profile is hi")

        def path = tmpdirPath as Path
        println("profile is hi")
        kodein = DependencyInjector.INSTANCE.initializeKodein(
                username,
                password,
                settings,
                path)
        extractor = new KodeinExtractor(kodein)
        extractor.db.launch()

        def res = extractor.profile.new(account) as Ior.Right<NonEmptyList<RuntimeError>, User>
        println("profile is $res")
        user = res.value
        extractor.profile.getUserDir(user, User.UserCategory.DataDir)
                .map { dataDir = it.toAbsolutePath() }
        Path file = FS.INSTANCE.get(this.class.getResource("/Infinity.pl").file)
        def ls = new ArrayList<String>()
        ls.add(file.toAbsolutePath().toString())
        extractor.proc.autoRun = false
        itasser = extractor.proc.new(
                UUID.randomUUID(),
                0,
                file,
                file.fileName.toString(),
                ls,
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
    ProcessPane create() {
        println("Dir is ${tmpdirPath.toAbsolutePath()}")
        extractor.profile.login(user, account.password as RawPassword, Duration.ofSeconds(0)) as Either.Right<ProfileManager.Profile>
        def view = new ProcessPane(new DI(kodein))
        return view
    }
}
