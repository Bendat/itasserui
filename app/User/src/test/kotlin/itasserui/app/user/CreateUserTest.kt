package itasserui.app.user

import arrow.data.Ior
import io.kotlintest.be
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import itasserui.app.user.ProfileError.*
import itasserui.common.`typealias`.Errors
import itasserui.common.`typealias`.ErrorsList
import itasserui.common.`typealias`.SoftErrors
import itasserui.common.errors.RuntimeError
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.Username
import itasserui.common.utils.Fake
import itasserui.lib.filemanager.FileManager
import itasserui.lib.filemanager.LocalFileManager
import itasserui.lib.store.Database.MemoryDatabase
import itasserui.test_utils.matchers.Be.none
import itasserui.test_utils.matchers.Be.ok
import org.dizitart.kno2.filters.eq
import java.nio.file.Files
import java.nio.file.Path

class CreateUserTest : DescribeSpec({
    System.setProperty("itasserui.directory.watch", false.toString())

    describe("Creating a user in an empty database") {
        val data = SetupObject()
        lateinit var user: Account
        it("Creates the user") {
            data.pm.createUserProfile(data.user)
                .map { user = it } should beInstanceOf<Ior.Right<RuntimeError, Account>>()
        }

        it("Checks the user against the database") {
            data.db.find<User> { User::id eq user.id } shouldBe ok()
        }

        context("Creating a duplicate user") {
            lateinit var retry: ErrorsList
            it("Attempts to add the same user to the database") {
                data.pm.createUserProfile(data.user)
                    .mapLeft { retry = it } should beInstanceOf<Errors>()
            }

            it("Verifies there is only one error") {
                retry.all.size should be(2)
            }

            it("Verifies the appropriate errors are present") {
                retry.all.any { it is CannotCreateUserProfileError } should be(true)
                retry.all.any { it is UserDirectoryAlreadyExists } should be(true)
            }
        }

        context("Creating user with existing username") {
            val userSameName = data.user.copy(emailAddress = EmailAddress(Fake.internet().emailAddress()))
            lateinit var errors: ErrorsList
            it("Tries to create the user") {
                data.pm.createUserProfile(userSameName)
                    .mapLeft { errors = it } should beInstanceOf<Errors>()
            }


            it("Prints the error") {
                println("Errors is $errors")
            }

            it("Verifies there is only one error") {
                errors.all.size should be(2)
            }

            it("Verifies the appropriate errors are present") {
                errors.all.any { it is CannotCreateUserProfileError } should be(true)
                errors.all.any { it is UserDirectoryAlreadyExists } should be(true)
            }

            it("Verifies the CannotCreateUserProfile error was caused by a duplicated username") {
                errors.all.first { it is CannotCreateUserProfileError }
                    .parentError should beInstanceOf<UsernameAlreadyExists>()
            }
        }

        context("Creating user with existing email address") {
            val userSameName = data.user.copy(username = Username(Fake.name().username()))
            lateinit var errors: ErrorsList
            it("Tries to create the user") {
                data.pm.createUserProfile(userSameName)
                    .mapLeft { errors = it; it } should beInstanceOf<SoftErrors>()
            }


            it("Prints the error") {
                println("Errors is $errors")
            }

            it("Verifies there is only one error") {
                errors.all.size should be(1)
            }

            it("Verifies the appropriate errors are present") {
                errors.all.any { it is CannotCreateUserProfileError } should be(true)
            }

            it("Verifies the CannotCreateUserProfile error was caused by a duplicated username") {
                errors.all.first { it is CannotCreateUserProfileError }
                    .parentError should beInstanceOf<UserEmailAlreadyExists>()
            }
        }

    }
})

private class SetupObject(testRootPath: Path = Files.createTempDirectory("pmtest")) {
    var fm: FileManager = LocalFileManager(testRootPath)
    val db = MemoryDatabase(Fake.name().username(), Fake.internet().password())
    val pm = ProfileManager(fm, db)
    val user = UserMocks.unregisteredUser

    init {
        db.launch() shouldBe none()
    }
}