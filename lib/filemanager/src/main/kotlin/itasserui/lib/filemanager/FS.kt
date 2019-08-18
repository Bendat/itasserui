@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package itasserui.lib.filemanager

import arrow.core.Try
import itasserui.common.`typealias`.Outcome
import itasserui.common.logger.Logger
import itasserui.lib.filemanager.FileDomain.Subcategory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.PosixFileAttributes

object FS {
    val create = Create
    val read = Read
    val update = Update
    val delete = Delete
    val itasserhome =
        Paths.get(System.getProperty("user.home"))
            .resolve("itasserui")

    operator fun get(path: String): Path = Paths.get(path)
    operator fun get(root: Path, vararg path: Subcategory): List<Path> =
        path.map { root.resolve(it.toString().toLowerCase()) }

    fun makeItasserHome() =
        create.directories(itasserhome)

    object Create : Logger {
        operator fun get(root: Path, vararg path: Subcategory): List<Path> =
            this[root, path.toList()]

        operator fun get(root: Path, paths: List<Subcategory>): List<Path> =
            paths.map { root.resolve(it.toString().toLowerCase()) }
                .map { directory(it); it }

        operator fun get(root: WatchedDirectory, vararg path: Subcategory): List<Path> =
            path.map { root.resolve(it.toString().toLowerCase()) }
                .map { directory(it); it }

        fun directories(path: Path): Outcome<Path> =
            Try { Files.createDirectories(path) }
                .toEither { CannotCreateFile(path, it) }

        fun directories(path: String): Outcome<Path> =
            directories(FS[path])

        fun directory(path: Path): Outcome<Path> =
            Try { Files.createDirectory(path) }
                .toEither { CannotCreateFile(path, it) }

        fun directory(path: String): Outcome<Path> =
            directory(FS[path])

        fun temp(path: String): Path =
            Files.createTempDirectory(path)
    }

    object Update {
        fun realPath(path: Path): Path = path.toRealPath()
    }

    object Read {
        fun exists(path: Path): Outcome<Boolean> =
            Try { Files.exists(path) }
                .toEither { FileSystemError.FileNotFoundError(path, it) }

        fun text(path: Path) =
            Try { Files.readAllLines(path) }
                .toEither()

        object Resource {
            operator fun get(path: String): Outcome<Path> =
                Try { FS[javaClass.getResource(path).file] }
                    .toEither { CannotRead(FS[path], it) }
        }

        object Attributes {
            fun basic(path: Path) =
                Try { Files.readAttributes(path, BasicFileAttributes::class.java) }

            fun posix(path: Path) =
                Try { Files.readAttributes(path, PosixFileAttributes::class.java) }
        }
    }

    object Delete {
        fun delete(path: Path) = Try { Files.delete(path) }
            .toEither { CannotDeleteFile(path, it) }
    }
}