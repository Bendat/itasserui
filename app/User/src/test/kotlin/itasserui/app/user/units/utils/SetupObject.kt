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
import java.nio.file.Path

internal class SetupObject(testRootPath: Path = FS.Create.temp("pmtest")) {
    var fm: FileManager = LocalFileManager(testRootPath)
    val db = MemoryDatabase(Fake.name().username(), Fake.internet().password())
    val pm = ProfileManager(fm, db)
    val user = UserMocks.unregisteredUser

    init {
        System.setProperty("itasserui.directory.watch", false.toString())
        db.launch() shouldBe none()
    }
}