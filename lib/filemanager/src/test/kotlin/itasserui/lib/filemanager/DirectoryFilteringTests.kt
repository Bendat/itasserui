package itasserui.lib.filemanager

import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.logger.Logger
import itasserui.common.utils.safeWait
import itasserui.common.utils.uuid
import itasserui.lib.filemanager.FileDomain.FileCategory.Test
import itasserui.lib.filemanager.utils.TestDomain
import itasserui.test_utils.matchers.Be
import java.nio.file.Files
import java.nio.file.Path



class LocalFileManagerTest : DescribeSpec({
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
            arrayListOf(1, 2, 3, 4).map { FileSystem[it.toString()] }.forEach {
                it("Adds path $it to the file manager") {
                    fm.new(testDomain)
                }
            }
        }

        context("Creating filler directories") {
            it("Creates the domain root") {
                fm.new(fillerDomain)
            }

            arrayListOf(1, 2, 3, 4).map { FileSystem[it.toString()] }.forEach {
                lateinit var watchedDirectory: WatchedDirectory
                lateinit var returnedDirectory: WatchedDirectory
                it("Adds path $it to the file manager") {
                    fm.new(fillerDomain)
                        .map { ret -> watchedDirectory = ret }
                }

                it("Should verify a WatchedDirectory is returned with get") {
                    fm[fillerDomain].map { ret ->
                        returnedDirectory = ret
                        return@map ret
                    } should Be.some()
                }

                it("Should verify the the two Watched Directories are the same") {
                    watchedDirectory should be(returnedDirectory)
                }
            }
        }

        context("Result verification") {
            it("Should verify the total size") {
                fm.size should be(9)
            }
        }
    }
})