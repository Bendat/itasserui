@file:Suppress("unused")

package itasserui.lib.filemanager

import arrow.core.Either
import arrow.core.Try
import itasserui.common.errors.Outcome
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFileAttributes

typealias FS = FileSystem

object FileSystem {
    operator fun get(path: String): Path = Paths.get(path)


    object Create {
        fun directories(path: Path) = Try { Files.createDirectories(path) }
            .toEither { CannotCreateFile(path, it) }

        fun directories(path: String) =
            directories(Paths.get(path))

    }

    object Read {
        fun exists(path: Path): Outcome<Boolean> = Try { Files.exists(path) }
            .toEither { FileSystemError.FileNotFoundError(path, it) }

        fun text(path: Path) = Try { Files.readAllLines(path) }
            .toEither()

        object Resource {
            operator fun get(path: String): Either<CannotRead, Path> {
                return Try { FS[javaClass.getResource(path).file] }
                    .toEither { CannotRead(Paths.get(path), it) }
            }
        }

        object Attributes {
            fun basic(path: Path) = Try { Files.readAttributes(path, BasicFileAttributes::class.java) }
            fun posix(path: Path) = Try { Files.readAttributes(path, PosixFileAttributes::class.java) }
        }
    }
}