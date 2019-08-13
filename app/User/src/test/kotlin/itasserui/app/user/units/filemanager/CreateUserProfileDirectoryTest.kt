package itasserui.app.user.units.filemanager

import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.app.user.UserMocks
import itasserui.app.user.units.utils.SetupObject
import itasserui.common.`typealias`.Err
import itasserui.common.`typealias`.OK
import itasserui.lib.filemanager.WatchedDirectory
import java.io.File

class CreateUserProfileDirectoryTest : DescribeSpec({
    describe("Setting up subcategory directories") {
        val data = SetupObject()
        val user = UserMocks.user
        lateinit var directory: WatchedDirectory
        it("Creates the base directory") {
            data.fm.new(user) should beInstanceOf<OK<*>>()
        }

        it("Creates the subcategories") {
            data.pm.setupUserDirectories(user)
                .map { directory = it } should beInstanceOf<OK<*>>()
        }



        it("Walks a tree") {
            print("Path is ${directory.path}")
            directory.path.toFile().walkTopDown().forEach { println("File [$it]") }
        }
        """
/var/folders/c8/5_6qs6r56_zcf48xm80ykmzr0000gn/T/pmtest6717340830936959465/users/brenna.hoppe/users/brenna.hoppe
  /var/folders/c8/5_6qs6r56_zcf48xm80ykmzr0000gn/T/pmtest6717340830936959465/users/brenna.hoppe/settings,
        """.trimIndent()
    }
})