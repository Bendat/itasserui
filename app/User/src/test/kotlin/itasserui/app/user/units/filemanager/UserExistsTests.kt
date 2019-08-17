package itasserui.app.user.units.filemanager

import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.app.user.units.utils.SetupObject
import itasserui.test_utils.matchers.Be

class UserExistsTests : DescribeSpec({
    val data = SetupObject()

    describe("User Exists Tests") {
        it("Verifies the profile manager cannot find an existing users file") {
            data.pm.existsInFileSystem(data.user) should be(false)
        }

        it("Creates a user file manually") {
            data.fm.new(data.user) should Be.ok()
        }

        it("Verifies the profile manager can find an existing users file") {
            data.pm.existsInFileSystem(data.user) should be(true)
        }
    }
})