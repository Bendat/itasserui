package itasserui.lib.filemanager.domainFileManager

import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.utils.uuid
import itasserui.lib.filemanager.DomainDirectory
import itasserui.lib.filemanager.DomainFileManager
import itasserui.lib.filemanager.FileDomain
import itasserui.lib.filemanager.utils.TestDomain
import java.nio.file.Files

class CreateDomainsTest : DescribeSpec({
    val root = Files.createTempDirectory("fmtest")
    describe("Creating a new domain") {
        val fm = DomainFileManager(root)
        lateinit var watchedDirectory: DomainDirectory
        val testDomain = TestDomain(
            FileDomain.FileCategory.Test,
            "testchild",
            uuid,
            TestCategories.values().toList()
        )

        it("Creates a new directory") {
            watchedDirectory = fm.new(testDomain)
        }

        it("Verifies the path of the testdomain") {
            watchedDirectory.unixPath should be(root.resolve(testDomain.relativeRoot))
        }

        it("Verifies the count") {
            watchedDirectory.count should be(3L)
        }
    }
})