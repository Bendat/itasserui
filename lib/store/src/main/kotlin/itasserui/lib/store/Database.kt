@file:Suppress("unused", "CanBeParameter")

package itasserui.lib.store

import arrow.core.*
import com.fasterxml.jackson.module.kotlin.KotlinModule
import itasserui.common.`typealias`.Outcome
import itasserui.common.interfaces.inline.Password
import itasserui.common.interfaces.inline.Username
import itasserui.common.logger.Logger
import itasserui.common.serialization.DBObject
import itasserui.common.serialization.Serializer
import itasserui.lib.filemanager.FS
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import org.dizitart.no2.mapper.JacksonFacade
import org.dizitart.no2.mapper.JacksonMapper
import org.dizitart.no2.objects.ObjectFilter
import org.dizitart.no2.objects.ObjectRepository
import java.nio.file.Files
import java.nio.file.Path

val memoryDbPath: Path = FS["itasser/memorydb.db"]

sealed class Database(
    val username: String,
    val password: String,
    val path: Path
) : AutoCloseable, Logger {

    protected var dbOptional: Option<Nitrite> = None
    val db get() = dbOptional

    abstract fun launch(): Option<DatabaseError>
    abstract fun init(): Database
    abstract fun deleteDatabase(): Option<DatabaseError>
    abstract fun exists(): Option<Boolean>

    inline fun <reified TRepo : DBObject> create(vararg item: TRepo) = insert(*item)
    inline fun <reified TRepo : DBObject> read(filter: () -> ObjectFilter) = find<TRepo>(filter)

    inline fun <reified TRepo : DBObject> size(): Outcome<Long> =
        perform<TRepo, Long> { it.getRepository<TRepo>().size() }

    inline fun <reified TRepo : DBObject> insert(vararg item: TRepo): Outcome<Long> {
        return perform<TRepo, Long> { it.getRepository<TRepo>().insert(item).affectedCount.toLong() }
    }

    inline fun <reified TRepo : DBObject, reified TReturn> perform(
        op: (Nitrite) -> TReturn
    ): Outcome<TReturn> {
        return db.toEither {
            info { "Database perform is $db" }
            NoDatabase(path)
        }.flatMap {
            when (val attempt = Try { op(it) }) {
                is Failure -> Left(DatabaseError.DatabaseAccessFailed(path, attempt.exception))
                is Success -> Right(attempt.value)
            }
        }.also {
            Try { db.map { i -> i.getRepository<TRepo>().close() } }
        }
    }

    inline fun <reified TRepo : DBObject> context(op: ObjectRepository<TRepo>.() -> Unit) =
        perform<TRepo, Unit> { it.getRepository<TRepo>().apply(op) }

    inline fun <reified TRepo : DBObject> update(obj: TRepo): Outcome<Long> =
        perform<TRepo, Long> { it.getRepository<TRepo>().update(DBObject::id eq obj.id, obj).affectedCount.toLong() }

    inline fun <reified TRepo : DBObject> delete(obj: TRepo): Outcome<Long> =
        perform<TRepo, Long> { it.getRepository<TRepo>().remove(DBObject::id eq obj.id).affectedCount.toLong() }

    inline fun <reified TRepo : DBObject> find(filter: () -> ObjectFilter): Outcome<List<TRepo>> =
        perform<TRepo, List<TRepo>> { it.getRepository<TRepo>().find(filter()).toList() }

    inline fun <reified TRepo : DBObject> find(): Outcome<List<TRepo>> =
        perform<TRepo, List<TRepo>> { it.getRepository<TRepo>().find().toList() }

    inline fun <reified T : DBObject> findFirst(): Outcome<T> {
        return perform<T, T> { it.getRepository<T>().find().first() }
    }

    override fun close() {
        db.map { it.close() }
    }


    class MemoryDatabase(
        username: String,
        password: String
    ) : Database(username, password, memoryDbPath) {
        override fun launch(): Option<InitError> =
            when (val result = Try {
                dbOptional = nitrite(userId = username, password = password) {
                    val facade = JacksonFacade(setOf(KotlinModule(), Serializer.Jackson.InlineModule))
                    nitriteMapper = facade
                }.some()
            }) {
                is Failure -> Some(InitError(result.exception))
                is Success -> None
            }


        @Suppress("ReplaceSingleLineLet")
        override fun deleteDatabase(): Option<DatabaseError> {
            return when (val db = this.db) {
                is Some -> db.t.close().let { None }
                is None -> None
            }
        }

        override fun exists(): Option<Boolean> {
            return db.map { true }
        }

        override fun init(): MemoryDatabase {
            db.map { it.close() }
            return this
        }
    }

    class PersistentDatabase(
        val directory: Path,
        private val name: String,
        username: String,
        password: String
    ) : Database(username, password, directory) {
        constructor(
            directory: Path,
            name: String,
            username: Username,
            password: Password
        ) : this(directory, name, username.value, password.value)

        val filePath get() = directory.resolve("$name.idb")

        override fun launch(): Option<InitError> {
            info { "Launching persistent database $filePath" }
            return when (val result = Try {
                dbOptional = nitrite(userId = username, password = password) {
                    file = filePath.toFile()
                    info { "File is $file" }
                    nitriteMapper = object : JacksonMapper() {
                        init {
                            objectMapper.registerModule(KotlinModule())
                            objectMapper.registerModule(Serializer.Jackson.InlineModule)
                        }
                    }
                }.some()
            }) {
                is Failure -> Some(InitError(result.exception))
                is Success -> None
            }
        }


        private val dbFile = directory.resolve(name)

        override fun deleteDatabase(): Option<DatabaseError> {
            info { "Deleting database [$db]" }
            return when (db) {
                is None -> Some(NoDatabase(dbFile))
                is Some -> when (val delete = Try { Files.delete(dbFile) }) {
                    is Failure -> Some(CannotDeleteDatabase(delete.exception))
                    is Success -> None
                }
            }

        }

        override fun exists(): Option<Boolean> =
            db.map { Files.exists(dbFile) }

        override fun init(): PersistentDatabase {
            db.map { it.close() }
            return this
        }
    }
}