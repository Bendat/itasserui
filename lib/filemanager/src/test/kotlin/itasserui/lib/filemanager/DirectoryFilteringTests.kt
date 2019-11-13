package itasserui.lib.filemanager

import arrow.core.getOrElse
import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.logger.Logger
import itasserui.common.utils.uuid
import itasserui.lib.filemanager.FileDomain.FileCategory.Test
import itasserui.lib.filemanager.utils.TestCategories
import itasserui.lib.filemanager.utils.TestDomain
import itasserui.test_utils.matchers.Be
import java.nio.file.Files


class DirectoryFilteringTests : DescribeSpec({
    val root = Files.createTempDirectory("fmtest")
    @Suppress("UNUSED_VARIABLE") val log = object : Logger {}
    System.setProperty("itasserui.directory.watch", true.toString())

    describe("Directory filtering by category") {
        val fm = LocalFileManager(root) as FileManager

        val testDomain = TestDomain(
            Test,
            relativeRootName = "testchild",
            id = uuid
        )

        val fillerDomain = TestDomain(
            category = Test,
            relativeRootName = "ignored",
            id = uuid
        )

        context("Creating a directory hierarchy under testDomain1") {
            it("Creates the domain root") {
                fm.new(testDomain, TestCategories.First)
            }

            arrayListOf(1, 2, 3, 4)
                .map { FS[it.toString()] }
                .forEach { path ->
                    it("Adds path $path to the file manager") {
                        fm.mkdirs(testDomain.copy(relativeRootName = testDomain.relativeRootName), path)
                    }
                }
        }

        context("Test domain should have 4 children") {
            it("Verifies the size of the test domain") {
                fm[testDomain]
                    .also { print("Domains are $it") }
                    .map { it.count }
                    .getOrElse { -1L } should be(4L)
            }
        }

        context("Creating filler directories") {
            it("Creates the domain root") {
                fm.new(fillerDomain, TestCategories.First)
            }

            arrayListOf(1, 2, 3, 4)
                .map { FS[it.toString()] }
                .forEach { path ->
                    lateinit var watchedDirectory: WatchedDirectory
                    lateinit var returnedDirectory: WatchedDirectory
                    it("Adds path $path to the file manager") {
                        fm.new(
                            fillerDomain.copy(relativeRootName = fillerDomain.relativeRootName + path),
                            TestCategories.First
                        ).map { ret -> watchedDirectory = ret }
                    }

                    it("Should verify a WatchedDirectory is returned with get") {
                        fm[fillerDomain].map { ret ->
                            returnedDirectory = ret
                            return@map ret
                        } should Be.some()
                    }

                    it("Should verify the the two Watched Directories are the same") {
                        watchedDirectory.root should be(returnedDirectory.root)
                    }
                }

            it("Filters by category") {
                println("Categories are ${fm[Test].map { it }}")
                fm[Test].size should be(6)
            }
        }
    }
})