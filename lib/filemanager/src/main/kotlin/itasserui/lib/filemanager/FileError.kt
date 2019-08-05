@file:Suppress("unused")

package itasserui.lib.filemanager

import itasserui.common.errors.RuntimeError
import java.nio.file.Path

typealias `404` = FileSystemError.FileNotFoundError
typealias FileNotFound = FileSystemError.FileNotFoundError
typealias CannotCreateFile = FileSystemError.CannotCreateFileError
typealias CannotDeleteFile = FileSystemError.CannotDeleteFileError
typealias CannotRead = FileSystemError.CannotCreateFileError

sealed class FileSystemError : RuntimeError() {
    abstract val file: Path
    abstract val exception: Throwable

    class FileNotFoundError(
        override val file: Path,
        override val exception: Throwable
    ) : FileSystemError()

    class CannotCreateFileError(
        override val file: Path,
        override val exception: Throwable
    ) : FileSystemError()

    class CannotDeleteFileError(
        override val file: Path,
        override val exception: Throwable
    ) : FileSystemError()

    class CannotReadFileError(
        override val file: Path,
        override val exception: Throwable
    ) : FileSystemError()
}