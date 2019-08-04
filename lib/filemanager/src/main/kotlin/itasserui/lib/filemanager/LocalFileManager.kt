package itasserui.lib.filemanager
import arrow.core.*
import io.methvin.watcher.DirectoryChangeEvent
import io.methvin.watcher.DirectoryWatcher
import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.list.ObservableListWrapper
import lk.kotlin.observable.list.filtering
import lk.kotlin.observable.list.observableListOf
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


class LocalFileManager(
    override val basedir: Path,
    inner: ObservableListWrapper<WatchedDirectory> = observableListOf()
) : FileManager {
    override val inner: ObservableList<WatchedDirectory> = inner

    override fun new(
        domain: FileDomain,
        new: Path,
        op: (DirectoryChangeEvent) -> Unit
    ): Either<FileSystemError, WatchedDirectory> {
        val dir = basedir.resolve(domain.directoryPath).resolve(new)
        return when (val res = FileSystem.Create.directories(dir)) {
            is Either.Left -> res
            is Either.Right -> watchDirectory(dir, domain.category, op).also {
                domain.directories = inner.filtering {
                    Files.exists(basedir.resolve(domain.directoryPath))
                }
            }.right()
        }
    }

    override fun delete(domain: FileDomain): Option<FileSystemError> {
        val dir = basedir.resolve(domain.directoryPath)
        return when (val res = Try { Files.delete(dir) }) {
            is Success -> None
            is Failure -> Some(FileSystemError.CannotDeleteFileError(dir, res.exception))
        }
    }

    override fun new(
        domain: FileDomain,
        op: (DirectoryChangeEvent) -> Unit
    ): Either<FileSystemError, WatchedDirectory> =
        new(domain, Paths.get(""), op)

    override fun watchDirectory(
        path: Path,
        category: FileDomain.FileCategory,
        op: (DirectoryChangeEvent) -> Unit
    ): WatchedDirectory {
        return DirectoryWatcher
            .builder()
            .path(path)
            .listener { event ->
                op(event)
                findByPath(event.path()).map { res -> res.update() }
            }.build()
            .let {
                WatchedDirectory(
                    path = path,
                    watcher = it,
                    future = it.watchAsync(),
                    category = category
                )
            }.also {
                inner += it
            }
    }

    private fun findByPath(path: Path): Option<WatchedDirectory> {
        return inner.firstOrNone { dir ->
            dir.toRealPath() == path.toRealPath()
        }
    }

    override fun wait(path: Path): Try<None> = Try {
        findByPath(path)
            .flatMap {
                it.watcher.watchAsync().getNow(null).toOption()
            } as None
    }

    override fun toString(): String {
        return "LocalFileManager(basedir=$basedir)"
    }

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            hook { "Closing directory watchers" }
            inner.forEach {
                it.watcher.close()
            }
        })
    }
}