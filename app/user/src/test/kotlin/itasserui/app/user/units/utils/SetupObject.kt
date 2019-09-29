package itasserui.app.user.units.utils

import io.kotlintest.shouldBe
import itasserui.app.user.ProfileManager
import itasserui.app.user.UserMocks
import itasserui.lib.filemanager.FileManager
import itasserui.lib.filemanager.LocalFileManager
import itasserui.lib.store.Database
import itasserui.lib.store.Database.PersistentDatabase
import itasserui.test_utils.matchers.Be
import org.kodein.di.Kodein
import org.kodein.di.conf.ConfigurableKodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.nio.file.Files
import java.nio.file.Path

@Suppress("MemberVisibilityCanBePrivate")
internal class SetupObject(val testRoot: Path = Files.createTempDirectory("itasser-test")) {
    val user = UserMocks.unregisteredUser
    private val testModule =
        Kodein.Module("Test Module") {
            val db = PersistentDatabase(testRoot, "Test Database", "Admin", "Admin")
                .also { it.launch() shouldBe Be.none() }
            val fm = LocalFileManager(testRoot)
            bind<Database>() with singleton { db }
            bind<FileManager>() with singleton { fm }
            bind<ProfileManager>() with singleton { ProfileManager(fm, db) }
        }
    val kodein = ConfigurableKodein(mutable = true).also { it.addImport(testModule) }
    val pm: ProfileManager by kodein.instance()

    init {
        System.setProperty("itasserui.directory.watch", false.toString())
    }


}