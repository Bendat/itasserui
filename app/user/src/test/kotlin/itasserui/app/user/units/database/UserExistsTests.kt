@file:Suppress("UNUSED_VARIABLE")

package itasserui.app.user.units.database

import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.app.user.ProfileError.UserEmailAlreadyExists
import itasserui.app.user.ProfileError.UsernameAlreadyExists
import itasserui.app.user.User
import itasserui.app.user.UserExists
import itasserui.app.user.UserMocks
import itasserui.app.user.units.utils.SetupObject
import itasserui.app.user.units.utils.testModule
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.Username
import itasserui.common.utils.Fake
import itasserui.test_utils.matchers.Be
import org.kodein.di.Kodein
import org.kodein.di.conf.global
import java.nio.file.Files

class UserExistsTests : DescribeSpec({
    val testRoot = Files.createTempDirectory("profile-test")
    Kodein.global.addImport(testModule(testRoot, "Admin", "Admin"))
    System.setProperty("itasserui.directory.watch", false.toString())


    describe("User Exists Tests") {
        val user = UserMocks.user
        val sameNameUser =
            User(user.id, user.username, user.password, EmailAddress(Fake.internet().emailAddress()))
        val sameEmailUser =
            User(user.id, Username(Fake.name().username()), user.password, user.emailAddress)
        val data = SetupObject()


        it("Writes a user directly to the database") {
            data.pm.database.create(user) should Be.ok()
        }

        it("Verifies the user is an exact match") {
            data.pm.userExists(user) should Be.some()
        }

        it("Verifies the user has a duplicate username") {
            data.pm.usernameExists(sameNameUser) should Be.some()
        }

        it("Verifies the user has a duplicate email address") {
            data.pm.emailExists(sameEmailUser) should Be.some()
        }

        context("ExistsInDatabase") {
            it("Verifies the user is an exact match") {
                data.pm.existsInDatabase(user) should Be.someOf<UserExists>()
            }

            it("Verifies the user has a duplicate username") {
                data.pm.existsInDatabase(sameNameUser) should Be.someOf<UsernameAlreadyExists>()
            }

            it("Verifies the user has a duplicate email address") {
                data.pm.existsInDatabase(sameEmailUser) should Be.someOf<UserEmailAlreadyExists>()
            }
        }
    }
})

