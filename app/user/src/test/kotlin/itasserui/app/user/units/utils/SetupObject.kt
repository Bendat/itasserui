package itasserui.app.user.units.utils

import io.kotlintest.shouldBe
import itasserui.app.user.ProfileManager
import itasserui.app.user.UserMocks
import itasserui.common.utils.Fake
import itasserui.lib.filemanager.FileManager
import itasserui.lib.filemanager.FS
import itasserui.lib.filemanager.LocalFileManager
import itasserui.lib.store.Database.MemoryDatabase
import itasserui.test_utils.matchers.Be.none
import org.kodein.di.Kodein
import org.kodein.di.conf.ConfigurableKodein
import org.kodein.di.conf.global
import java.nio.file.Files
import java.nio.file.Path

internal class SetupObject() {
    val testRoot = Files.createTempDirectory("profile-test")
    val user = UserMocks.unregisteredUser
    val module get() = testModule(testRoot, "Admin", "Admin")
    val kodein = ConfigurableKodein()
    val pm = ProfileManager(kodein)
    init {
        kodein.addImport(module)
        System.setProperty("itasserui.directory.watch", false.toString())
    }
}