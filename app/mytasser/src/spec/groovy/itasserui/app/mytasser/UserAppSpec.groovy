package itasserui.app.mytasser

import arrow.data.Ior
import arrow.data.NonEmptyList
import itasser.app.mytasser.kodeinmodules.DependencyInjector
import itasserui.app.mytasser.lib.DI
import itasserui.app.mytasser.lib.ITasserSettings
import itasserui.app.user.UnregisteredUser
import itasserui.app.user.User
import itasserui.common.errors.RuntimeError
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.lib.filemanager.FS
import itasserui.lib.process.process.ITasser
import javafx.scene.control.TextInputControl
import javafx.scene.input.KeyCode
import org.kodein.di.Kodein
import tornadofx.Scope
import tornadofx.UIComponent

import java.nio.file.Path

import static itasserui.common.utils.FakeKt.Fake
import static java.util.UUID.randomUUID

abstract class UserAppSpec<T extends UIComponent> extends AppSpec<T> {
    private ITasserSettings settings = new ITasserSettings(tmpdirPath, libdir, javaHome, datadir, "gnuparallel", randomUUID())
    Path dataDir = null
    UnregisteredUser account = new UnregisteredUser(
            new Username(Fake.name().username()),
            new RawPassword(Fake.internet().password()),
            new EmailAddress(Fake.internet().emailAddress()),
            false)
    User user = null
    Kodein kodein = null
    KodeinExtractor extractor = null
    Path file = FS.INSTANCE.get(this.class.getResource("/run-ITASSER.pl").file)
    Path seqFile = FS.INSTANCE.get(this.class.getResource("/seq.fasta").file)
    Scope testScope = new Scope()

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
        def path = tmpdirPath as Path
        kodein = DependencyInjector
                .INSTANCE.
                initializeKodein(username, password, settings, path)
        extractor = new KodeinExtractor(kodein)
        extractor.db.launch()
        user = createUser(extractor)
        extractor.profile.getUserDir(user, User.UserCategory.DataDir)
                .map { dataDir = it.toAbsolutePath() }
        def ls = new ArrayList<String>()
        ls.add(file.toAbsolutePath().toString())
        extractor.proc.autoRun = false
        def di = new DiInitializer(testScope)
        di.setInScope(new DI(kodein), testScope)
    }

    @SuppressWarnings("unused")
    private ITasser newProcess(KodeinExtractor extractor, Path file, ArrayList<String> ls, User user) {
        extractor.proc.new(randomUUID(), 0, file, file.fileName.toString(), ls, user.id, dataDir)
    }

    protected User createUser(KodeinExtractor extractor) {
        def res = extractor.profile.new(account) as Ior.Right<NonEmptyList<RuntimeError>, User>
        res.value
    }

    protected void loginWithModal(Closure<TextInputControl> loginUserPassword) {
        clickOn(loginUserPassword()).write(account.password.value)
        for (int i = 0; i < 30; i++) {
            clickOn(".login-modal .increment-arrow-button")
        }

        clickOn("#login_login")
    }

    @SuppressWarnings("unused")
    protected Kodein initKodein(Path path) {
        def kodein = DependencyInjector.INSTANCE.initializeKodein(
                username,
                password,
                settings,
                path)
        return kodein
    }
}