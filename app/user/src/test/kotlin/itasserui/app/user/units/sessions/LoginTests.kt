package itasserui.app.user.units.sessions

import arrow.data.Ior.Right
import io.kotlintest.be
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.app.user.Account
import itasserui.app.user.ProfileManager.Profile
import itasserui.app.user.units.utils.SetupObject
import itasserui.common.errors.RuntimeError
import itasserui.common.utils.safeWait
import itasserui.test_utils.matchers.Be
import java.time.Duration

class LoginTests : DescribeSpec({
    describe("Creating a new user") {
        val data = SetupObject()
        lateinit var user: Account
        it("Creates the user") {
            data.pm.createUserProfile(data.user)
                .map { user = it } should beInstanceOf<Right<RuntimeError, Account>>()
        }

        context("Logging in with no session time") {
            lateinit var profile: Profile
            it("Attempts to login") {
                data.pm
                    .login(user, data.user.password)
                    .map { profile = it }
                    .mapLeft { print("Error: $it") }
            }

            it("Verifies the user matches the session user") {
                profile.user should be(user)
            }

            it("Verifies the session is inactive") {
                profile.session.map { it.isActive should be(false) } should Be.some()
            }
        }

        context("Logging in with an active session") {
            lateinit var profile: Profile

            it("Attempts to login") {
                data.pm
                    .login(user, data.user.password, Duration.ofMillis(500))
                    .map { profile = it }
                    .mapLeft { print("Error: $it") }
            }

            it("Verifies the session is active") {
                profile.session.map { it.isActive should be(true) } should Be.some()
            }

            it("Waits 200 milliseconds") {
                safeWait(200)
            }

            it("Verifies the session is still active") {
                profile.session.map { it.isActive should be(true) } should Be.some()
            }

            context("Session should become inactive after its duration has passed") {
                it("Waits a further 400 milliseconds") {
                    safeWait(400)
                }

                it("Verifies the session is no longer active") {
                    profile.session.map { it.isActive should be(false)} should Be.some()
                }
            }
        }
    }
})