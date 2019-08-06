package itasserui.lib.store

import itasserui.common.errors.RuntimeError
import java.nio.file.Path

typealias DBError = DatabaseError
typealias InitError = DatabaseError.InitializationException
typealias NoDatabase = DatabaseError.NoDatabaseError
typealias CannotDeleteDatabase = DatabaseError.CannotDeleteDatabaseError
sealed class DatabaseError: RuntimeError() {
    class InitializationException(val exception: Throwable) : DatabaseError()
    class CannotDeleteDatabaseError(val exception: Throwable): DatabaseError()
    class NoDatabaseError(val path: Path): DatabaseError()
    class DatabaseAccessFailed(val path: Path, val exception: Throwable) : DatabaseError()
}