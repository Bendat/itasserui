package itasserui.app.user.units.filemanager

import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.app.user.UserMocks
import itasserui.app.user.units.utils.SetupObject
import itasserui.common.`typealias`.OK

class CreateUserProfileDirectoryTest : DescribeSpec({
    describe("Setting up subcategory directories") {
        val data = SetupObject()
        val user = UserMocks.unregisteredUser
        it("Creates the base directory") {
            data.fm.new(user) should beInstanceOf<OK<*>>()
        }

        it("Creates the subcategories") {
            data.pm.setupUserDirectories(user) should beInstanceOf<OK<*>>()
        }
    }
})