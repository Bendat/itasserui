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
import org.kodein.di.conf.global
import java.nio.file.Files
import java.nio.file.Path

internal class SetupObject() {
    val testRoot = Files.createTempDirectory("profile-test")
    val pm = ProfileManager()
    val user = UserMocks.unregisteredUser

    init {
        Kodein.global.addImport(testModule(testRoot, "Admin", "Admin"))
        System.setProperty("itasserui.directory.watch", false.toString())
    }
}