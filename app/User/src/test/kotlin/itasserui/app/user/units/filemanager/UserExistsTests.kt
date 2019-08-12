package itasserui.app.user.units.filemanager

import io.kotlintest.be
import io.kotlintest.eventually
import io.kotlintest.seconds
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.app.user.ProfileManager
import itasserui.app.user.User
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.utils.Fake
import itasserui.common.utils.uuid
import itasserui.lib.filemanager.FileDomain.FileCategory.Test
import itasserui.lib.filemanager.FileManager
import itasserui.lib.filemanager.LocalFileManager
import itasserui.lib.store.Database
import itasserui.lib.store.Database.MemoryDatabase
import itasserui.test_utils.matchers.Be
import java.nio.file.Files
import java.nio.file.Path

class UserExistsTests : DescribeSpec({
    lateinit var testRootPath: Path
    lateinit var fm: FileManager
    eventually(1.seconds) {
        testRootPath = Files.createTempDirectory("pmtest")
        System.setProperty("itasserui.directory.watch", false.toString())
        fm = LocalFileManager(testRootPath)
    }

    describe("User Exists Tests") {
        lateinit var db: Database
        lateinit var pm: ProfileManager
        val user = User(
            uuid,
            Username(Fake.name().username()),
            RawPassword(Fake.internet().password()).hashed,
            EmailAddress(Fake.internet().emailAddress())
        )

        eventually(1.seconds) {
            db = MemoryDatabase(Fake.name().username(), Fake.internet().password())
            pm = ProfileManager(fm, db)
        }

        it("Verifies the profile manager cannot find an existing users file") {
            pm.existsInFileSystem(user) should be(false)
        }

        it("Creates a user file manually") {
            fm.new(user) should Be.ok()
        }

        it("Verifies the profile manager can find an existing users file") {
            pm.existsInFileSystem(user) should be(true)
        }
    }
})