package itasserui.common.errors

import arrow.core.Either
import java.nio.file.Path

typealias SequenceResult<T> = Either<SequenceError, T>

typealias BadChar = SequenceError.ParseError.InvalidCharError
typealias NoStartingHeader = SequenceError.ParseError.NoStartingDescriptionError
typealias SeqReadError = SequenceError.IOError.FileReadError
typealias EmptyFile = SequenceError.IOError.EmptyFileError
typealias SizeMismatch = SequenceError.ParseError.SizeMismatchError
typealias NoSuchFile = SequenceError.IOError.NoSuchFileError
typealias FileIsDirectory = SequenceError.IOError.FileIsDirectoryError
typealias InvalidFile = SequenceError.IOError.InvalidFileNameError

sealed class SequenceError : RuntimeError() {

    class UnknownExceptionError(
        val exception: Throwable
    ) : SequenceError()

    sealed class ParseError : SequenceError() {
        abstract val file: Path?

        class InvalidCharError(
            val title: String,
            val sequence: String?,
            val badChars: Set<Char>,
            override val file: Path?
        ) : ParseError()

        class SizeMismatchError(
            val descriptionCount: Int,
            val bodyCount: Int,
            override val file: Path?
        ) : ParseError()

        class NoStartingDescriptionError(
            override val file: Path?
        ) : ParseError()

    }

    sealed class IOError : SequenceError() {
        abstract val file: Path?

        class FileReadError(
            override val file: Path?
        ) : IOError()

        class InvalidFileNameError(
            override val file: Path?
        ) : IOError()

        class NoSuchFileError(
            override val file: Path?
        ) : IOError()

        class FileIsDirectoryError(
            override val file: Path?
        ) : IOError()

        class EmptyFileError(
            override val file: Path?
        ) : IOError()
    }

}