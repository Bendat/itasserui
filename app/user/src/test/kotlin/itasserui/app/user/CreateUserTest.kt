package itasserui.app.user

import arrow.data.Ior
import io.kotlintest.be
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import itasserui.app.user.ProfileError.*
import itasserui.app.user.units.utils.SetupObject
import itasserui.common.`typealias`.Errors
import itasserui.common.`typealias`.ErrorsList
import itasserui.common.`typealias`.SoftErrors
import itasserui.common.errors.RuntimeError
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.Username
import itasserui.common.utils.Fake
import itasserui.test_utils.matchers.Be.ok
import org.dizitart.kno2.filters.eq


class CreateUserTest : DescribeSpec({
    describe("Creating a user in an empty database") {
        val data = SetupObject()
        lateinit var user: Account
        it("Creates the user") {
            data.pm.new(data.user)
                .map { user = it } should beInstanceOf<Ior.Right<RuntimeError, Account>>()
        }

        it("Checks the user against the database") {
            data.pm.database.find<User> { User::id eq user.id } shouldBe ok()
        }

        it("Verifies the users directory path") {
            data.pm.fileManager.fullPath(user) should be(
                data.pm.fileManager.basedir.resolve("${user.category}/${user.username.value}")
            )
        }

        context("Creating a duplicate user") {
            lateinit var retry: ErrorsList
            it("Attempts to add the same user to the database") {
                data.pm.new(data.user)
                    .mapLeft { retry = it } should beInstanceOf<Errors>()
            }

            it("Verifies there is only one error") {
                retry.all.size should be(2)
            }

            it("Verifies the ${CannotCreateUserProfileError::class.simpleName} error is present resent") {
                retry.all.any { it is CannotCreateUserProfileError } should be(true)
            }

            it("Verifies the ${UserDirectoryAlreadyExists::class.simpleName} error is present resent") {
                retry.all.any { it is UserDirectoryAlreadyExists } should be(true)
            }
        }

        context("Creating user with existing username") {
            val userSameName = data.user.copy(emailAddress = EmailAddress(Fake.internet().emailAddress()))
            lateinit var errors: ErrorsList
            it("Tries to create the user") {
                data.pm.new(userSameName)
                    .mapLeft { errors = it } should beInstanceOf<Errors>()
            }

            it("Verifies there is only one error") {
                errors.all.size should be(2)
            }

            it("Verifies the ${CannotCreateUserProfileError::class.simpleName} errors are present") {
                errors.all.any { it is CannotCreateUserProfileError } should be(true)
            }
            it("Verifies the ${UserDirectoryAlreadyExists::class.simpleName} errors are present") {
                errors.all.any { it is UserDirectoryAlreadyExists } should be(true)
            }

            it("Verifies the ${CannotCreateUserProfileError::class.simpleName} error was caused by a duplicated username") {
                errors.all.first { it is CannotCreateUserProfileError }
                    .parentError should beInstanceOf<UsernameAlreadyExists>()
            }
        }

        context("Creating user with existing email address") {
            val userSameName = data.user.copy(username = Username(Fake.name().username()))
            lateinit var errors: ErrorsList
            it("Tries to create the user") {
                data.pm.new(userSameName)
                    .mapLeft { errors = it; it } should beInstanceOf<SoftErrors>()
            }

            it("Verifies there is only one error") {
                errors.all.size should be(1)
            }

            it("Verifies the the ${CannotCreateUserProfileError::class.simpleName} error is present") {
                errors.all.any { it is CannotCreateUserProfileError } should be(true)
            }

            it("Verifies the CannotCreateUserProfile error was caused by a duplicated username") {
                errors.all.first { it is CannotCreateUserProfileError }
                    .parentError should beInstanceOf<UserEmailAlreadyExists>()
            }
        }
    }
})

