package itasserui.lib.store

import arrow.core.None
import arrow.core.Option
import arrow.core.firstOrNone
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.kotlintest.be
import io.kotlintest.eventually
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.seconds
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.`typealias`.Err
import itasserui.common.`typealias`.OK
import itasserui.common.extensions.print
import itasserui.common.serialization.DBObject
import itasserui.common.utils.uuid
import itasserui.lib.store.Database.PersistentDatabase
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.objects.Cursor
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

data class TestDBObject(override val id: UUID, val foo: String = "Bar") : DBObject

class DatabaseTest : DescribeSpec({
    val testObject = TestDBObject(uuid)

    describe("Standard tests using in memory Database instance.") {
        val db = Database.MemoryDatabase("uname", "pass")

        it("Starts the database") {
            db.launch().print() as None
        }

        context("Create") {
            it("Creates a valid TestObject $testObject") {
                db.create(testObject).print() as OK
            }
        }

        context("Read") {
            lateinit var toResult: Cursor<TestDBObject>
            context("Reading existing repo") {
                it("Reads the $testObject from the database") {
                    db.read<TestDBObject> { TestDBObject::id eq testObject.id }
                        .map { toResult = it }
                }

                it("Verifies the $testObject") {
                    toResult.first().print() should be(testObject)
                }
            }
        }

        context("Update") {
            val newTestObject = testObject.copy(foo = "rab")
            lateinit var toResult: Cursor<TestDBObject>
            context("Successfully") {
                it("The $testObject with $newTestObject") {
                    db.update(newTestObject)
                }

                it("Retrieves the $testObject from the database") {
                    db.read<TestDBObject> { TestDBObject::id eq newTestObject.id }
                        .map { toResult = it }
                }

                it("Verifies the $testObject") {
                    toResult.first().print() should be(newTestObject)
                }
            }

            context("Unsuccessfully") {
                it("The $testObject with $newTestObject") {
                    db.update(newTestObject)
                }

                it("The $testObject from the database") {
                    db.read<TestDBObject> { TestDBObject::id eq uuid }
                        .map { toResult = it }
                }

                it("Verifies the $testObject") {
                    toResult.firstOrNone() should be<Option<*>>(None)
                }
            }
        }

        context("Delete") {
            lateinit var toResult: Cursor<TestDBObject>
            it("The $testObject from the database") {
                db.delete(testObject)
            }
            it("Attempts to extract deleted object $testObject from the database") {
                db.read<TestDBObject> { TestDBObject::id eq testObject.id }
                    .map { toResult = it }
            }
            it("Verifies the $testObject was deleted") {
                toResult.size() should be(0)
            }
        }
    }

    describe("IOException tests using real database") {
        context("Verifying JimFS works as expected") {
            lateinit var db: PersistentDatabase
            lateinit var tmpRoot: Path
            val fs = Jimfs.newFileSystem(Configuration.unix())

            val foo = fs.getPath("/databasetest")
            eventually(1.seconds) {
                tmpRoot = Files.createDirectory(foo)
                db = PersistentDatabase(tmpRoot, "db", "user", "user")
                db.launch()
            }

            it("Writes to the non existent db") {
                db.create(testObject) should beInstanceOf<OK<*>>()
            }

            eventually(1.seconds) {
                fs.close()
            }

        }

        context("Deleting the file mid test") {
            lateinit var db: PersistentDatabase
            lateinit var tmpRoot: Path
            val fs = Jimfs.newFileSystem(Configuration.unix())

            val foo = fs.getPath("/databasetest2")
            eventually(1.seconds) {
                tmpRoot = Files.createDirectory(foo)
                db = PersistentDatabase(tmpRoot, "db", "user", "user")
                db.launch()
                Files.delete(tmpRoot)
                db.db.map { it.close() }
            }

            it("Writes to the non existent db") {
                db.find<TestDBObject> { DBObject::id eq testObject.id } should beInstanceOf<Err>()
            }

            eventually(1.seconds) {
                fs.close()
            }
        }
    }
})

