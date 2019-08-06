package itasserui.lib.filemanager.filesystem

import io.kotlintest.be
import io.kotlintest.eventually
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.seconds
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.`typealias`.OK
import itasserui.lib.filemanager.FS
import itasserui.lib.filemanager.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFileAttributes

class FileSystemTest : DescribeSpec({
    describe("Retrieving a Path from the FileSystem object") {
        context("Path which exists") {
            val dir = System.getProperty("user.dir")
            lateinit var path: Path
            it("Gets the user.dir directory") {
                path = FS[dir]
            }

            it("Verifies the path exists") {
                Files.exists(path) should be(true)
            }

            it("Verifies the path is absolute") {
                path.isAbsolute should be(true)
            }
        }

        context("Path which does not yet exist") {
            val dir = "relative/dir"
            lateinit var path: Path

            it("Gets the path for $dir") {
                path = FS[dir]
            }

            it("Verifies the path exists") {
                println("Hello there")
                Files.exists(path) should be(false)
            }

            it("Verifies the path is absolute") {
                path.isAbsolute should be(false)
            }
        }
    }

    describe("Create") {
        context("A new directory") {
            lateinit var tmp: Path
            lateinit var result: Path
            eventually(1.seconds) {
                tmp = Files.createTempDirectory("fstest")
            }

            it("Prints the path") {
                FileSystem.Create.directories(tmp.resolve("oppa/goppa/doppa"))
                    .map { result = it }
            }

            it("Verifies the directory exists") {
                Files.exists(result) should be(true)
            }
        }
    }

    describe("Read") {
        context("General read operations") {
            context("Exists") {
                lateinit var tmp: Path
                eventually(1.seconds) {
                    tmp = Files.createTempDirectory("fstest")
                }

                it("Verifies the tmp directory exsits") {
                    FileSystem.Read.exists(tmp) should beInstanceOf<OK<Boolean>>()
                }
            }

            context("Text") {
                val textFile = FS[javaClass.getResource("/hello.txt").file]

                it("Retrieves the text") {
                    FileSystem.Read.text(textFile)
                        .map { it.size should be(3) } as OK
                }
            }
        }

        context("Resource") {
            it("Accesses a resource") {
                FileSystem.Read.Resource["/hello.txt"] as OK
            }
        }

        context("Attributes") {
            lateinit var path: Path
            lateinit var attributes: PosixFileAttributes
            it("Retrieves the file") {
                FileSystem.Read.Resource["/hello.txt"]
                    .map { path = it }
            }

            context("Basic") {
                it("Gets the basic attributes") {
                    FileSystem.Read.Attributes.posix(path)
                        .map { attributes = it }
                }

                it("Verifies the file is not a directory") {
                    attributes.isDirectory should be(false)
                }
            }
        }
    }
})