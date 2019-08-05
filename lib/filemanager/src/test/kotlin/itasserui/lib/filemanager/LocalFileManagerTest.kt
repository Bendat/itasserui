package itasserui.lib.filemanager

import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.`typealias`.OK
import itasserui.common.logger.Logger
import itasserui.common.utils.safeWait
import itasserui.common.utils.uuid
import itasserui.lib.filemanager.utils.TestDomain
import lk.kotlin.observable.list.observableListOf
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class LocalFileManagerTest : DescribeSpec({
    val root = Files.createTempDirectory("fmtest")
    @Suppress("UNUSED_VARIABLE") val log = object : Logger {}

    describe("Creating a new directory") {
        val fm = LocalFileManager(root) as FileManager

        val testDomain = TestDomain(
            FileDomain.FileCategory.Test,
            "testchild",
            observableListOf(),
            uuid
        )

        it("Creates a new directory") {
            fm.new(
                domain = testDomain,
                new = FileSystem["first"]
            ) as OK
        }

        it("Creates new subdirectory") {
            fm.new(testDomain, FileSystem["first/2"])
            safeWait(100)
        }

        it("Validates the correct number of entries") {
            testDomain.directories.size should be(2)
        }
    }

    describe("Watching a directory") {
        val fm = LocalFileManager(root) as FileManager

        val path = root.resolve("test1")
        lateinit var eventPath: Path
        it("Creates a watcher") {
            fm.watchDirectory(
                root,
                FileDomain.FileCategory.Test
            ) { event ->
                if (event.path().toRealPath() == path.toRealPath())
                    eventPath = event.path().toRealPath()
            }
        }

        it("Creates a new path") {
            FileSystem.Create.directories(path)
            safeWait(200)
        }

        it("Verifies the created path") {
            eventPath should be(FileSystem.Update.realPath(path))
        }
    }

    describe("Filtering") {
        val fm = LocalFileManager(root) as FileManager

        val testDomain = TestDomain(
            category = FileDomain.FileCategory.Test,
            directoryName = "testchild",
            directories = observableListOf(),
            id = uuid
        )

        val fillerDomain = TestDomain(
            category = FileDomain.FileCategory.Test,
            directoryName = "ignored",
            directories = observableListOf(),
            id = uuid
        )

        context("Creating a directory hierarchy under testDomain1") {
            it("Creates the domain root") {
                fm.new(testDomain)
            }

            arrayListOf(1, 2, 3, 4).map { Paths.get(it.toString()) }.forEach {
                it("Adds path $it to the file manager") {
                    fm.new(testDomain, it)
                }
            }
        }

        context("Creating filler directories") {
            it("Creates the domain root") {
                fm.new(fillerDomain)
            }

            arrayListOf(1, 2, 3, 4).map { Paths.get(it.toString()) }.forEach {
                it("Adds path $it to the file manager") {
                    fm.new(fillerDomain, it)
                }
            }
        }

        context("Result verification") {
            it("Should verify the total size") {
                fm.size should be(10)
            }

            it("Should get only those elements who are part of 'testDomain'") {
                fm[testDomain].size should be(5)
            }
        }
    }
})