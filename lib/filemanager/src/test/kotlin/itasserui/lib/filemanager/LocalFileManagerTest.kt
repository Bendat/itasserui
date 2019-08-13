package itasserui.lib.filemanager

import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.`typealias`.OK
import itasserui.common.logger.Logger
import itasserui.common.utils.safeWait
import itasserui.common.utils.uuid
import itasserui.lib.filemanager.FileDomain.FileCategory.Test
import itasserui.lib.filemanager.utils.TestDomain
import itasserui.test_utils.matchers.Be
import lk.kotlin.observable.list.observableListOf
import java.nio.file.Files
import java.nio.file.Path

enum class TestCategories : FileDomain.Subcategory {
    First,
    Second,
    Third,
}

class LocalFileManagerTest : DescribeSpec({
    val root = Files.createTempDirectory("fmtest")
    @Suppress("UNUSED_VARIABLE") val log = object : Logger {}
    System.setProperty("itasserui.directory.watch", true.toString())
    describe("Creating a new directory") {
        val fm = LocalFileManager(root) as FileManager

        val testDomain = TestDomain(
            Test,
            "testchild",
            observableListOf(),
            uuid
        )

        it("Creates a new directory") {
            fm.new(domain = testDomain) as OK
        }

        it("Creates numeric subcategories") {
            fm[testDomain, TestCategories.values().toList()]
        }

        it("Validates the correct number of entries") {
            safeWait(500)
            println("Blah blah is ${testDomain.directories.map { it }}")
            testDomain.directories.size should be(3)
        }

        it("Verifies the directory exists") {
            fm.exists(testDomain) should be(true)
        }

        it("Verifies a non existent domain is not returned") {
            fm.exists(testDomain.copy(relativeRootName = "Nope")) should be(false)
        }

    }

    describe("Watching a directory") {
        val fm = LocalFileManager(root) as FileManager
        val path = root.resolve("test1")
        val testDomain = TestDomain(
            Test,
            "test1",
            observableListOf(),
            uuid
        )
        lateinit var eventPath: Path
        it("Creates the path") {
            eventPath = Files.createDirectories(path)
        }
        it("Creates a watcher") {
            fm.watchDirectory(
                path,
                testDomain.category
            ) { event ->
                if (event.path().toRealPath() == path.toRealPath())
                    eventPath = event.path().toRealPath()
            }
        }

        it("Creates a new path") {
            FileSystem.Create.directories(path) should Be.ok()
            safeWait(500)
        }

        it("Verifies the created path") {
            FileSystem.Update.realPath(eventPath).toString() should be(FileSystem.Update.realPath(path).toString())
        }
    }

    describe("Filtering") {
        val fm = LocalFileManager(root) as FileManager

        val testDomain = TestDomain(
            Test,
            relativeRootName = "testchild",
            directories = observableListOf(),
            id = uuid
        )

        val fillerDomain = TestDomain(
            category = Test,
            relativeRootName = "ignored",
            directories = observableListOf(),
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
                        returnedDirectory = ret;
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