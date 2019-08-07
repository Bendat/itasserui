@file:Suppress("UNUSED_VARIABLE")

package itasserui.app.user

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.kotlintest.eventually
import io.kotlintest.seconds
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.utils.Fake
import itasserui.common.utils.uuid
import itasserui.lib.filemanager.FileManager
import itasserui.lib.filemanager.LocalFileManager
import itasserui.lib.store.Database
import itasserui.lib.store.Database.MemoryDatabase
import itasserui.test_utils.matchers.Be
import java.nio.file.Path

class ProfileManagerTest : DescribeSpec({
    val jimFS = Jimfs.newFileSystem(Configuration.unix())
    lateinit var testRootPath: Path
    lateinit var fm: FileManager
    eventually(1.seconds) {
        testRootPath = jimFS.getPath("/")
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

        val sameNameUser = user.copy(emailAddress = EmailAddress(Fake.internet().emailAddress()))
        val sameEmailUser = user.copy(username = Username(Fake.name().username()))

        eventually(1.seconds) {
            db = MemoryDatabase(Fake.name().username(), Fake.internet().password())
            pm = ProfileManager(fm, db)
        }

        it("Writes a user directly to the database") {
            db.launch() should Be.none()
            db.create(user) should Be.ok()
        }

        it("Verifies the user is an exact match") {
            pm.userExists(user) should Be.some()
        }

        it("Verifies the user has a duplicate username") {
            pm.usernameExists(sameNameUser) should Be.some()
        }

        it("Verifies the user has a duplicate email address") {
            pm.emailExists(sameEmailUser) should Be.some()
        }


    }


})

