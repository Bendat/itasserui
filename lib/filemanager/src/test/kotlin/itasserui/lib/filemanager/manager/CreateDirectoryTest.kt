package itasserui.lib.filemanager.manager

import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.`typealias`.OK
import itasserui.common.logger.Logger
import itasserui.common.utils.safeWait
import itasserui.common.utils.uuid
import itasserui.lib.filemanager.FileDomain.FileCategory.Test
import itasserui.lib.filemanager.FileManager
import itasserui.lib.filemanager.LocalFileManager
import itasserui.lib.filemanager.WatchedDirectory
import itasserui.lib.filemanager.utils.TestDomain
import java.nio.file.Files

class CreateDirectoryTest : DescribeSpec({
    val root = Files.createTempDirectory("fmtest")
    System.setProperty("itasserui.directory.watch", true.toString())
    @Suppress("UNUSED_VARIABLE")
    val log = object : Logger {}

    describe("Creating a new directory") {
        val fm = LocalFileManager(root) as FileManager
        lateinit var watchedDirectory: WatchedDirectory
        val testDomain = TestDomain(
            Test,
            "testchild",
            uuid
        )

        it("Creates a new directory") {
            fm.new(domain = testDomain)
                .map { watchedDirectory = it } as OK
        }

        it("Creates numeric subcategories") {
            fm[testDomain, TestCategories.values().toList()]
        }

        it("Validates the c orrect number of entries") {
            safeWait(100)
            watchedDirectory.count should be(3L)
        }

        it("Verifies the directory exists") {
            fm.exists(testDomain) should be(true)
        }

        it("Verifies a non existent domain is not returned") {
            fm.exists(testDomain.copy(relativeRootName = "Nope")) should be(false)
        }
    }
})