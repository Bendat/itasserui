@file:Suppress("unused")

package itasserui.lib.filemanager

import arrow.core.*
import io.methvin.watcher.DirectoryChangeEvent
import io.methvin.watcher.DirectoryWatcher
import itasserui.common.`typealias`.Outcome
import itasserui.lib.filemanager.FileDomain.FileCategory
import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.list.ObservableListWrapper
import lk.kotlin.observable.list.filtering
import lk.kotlin.observable.list.observableListOf
import java.nio.file.Files
import java.nio.file.Path


class LocalFileManager(
    override val basedir: Path,
    inner: ObservableListWrapper<WatchedDirectory> = observableListOf()
) : FileManager {
    override val inner: ObservableList<WatchedDirectory> = inner

    override fun new(
        domain: FileDomain,
        new: Path,
        op: (DirectoryChangeEvent) -> Unit
    ): Outcome<WatchedDirectory> {
        val dir = fullPath(domain).resolve(new)
        return when (val res = FileSystem.Create.directories(dir)) {
            is Either.Left -> res
            is Either.Right -> watchDirectory(dir, domain.category, op).also {
                domain.directories = inner.filtering {
                    Files.exists(fullPath(domain))
                }
            }.right()
        }
    }

    override fun new(
        domain: FileDomain,
        op: (DirectoryChangeEvent) -> Unit
    ): Outcome<WatchedDirectory> {
        return new(domain, domain.relativeRoot, op)
    }


    override fun wait(path: Path): Try<None> = Try {
        findByPath(path)
            .flatMap {
                it.watcher.flatMap { dir -> dir.watchAsync().getNow(null).toOption() }
            } as None
    }

    override fun toString(): String {
        return "LocalFileManager(basedir=$basedir)"
    }

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            hook { "Closing directory watchers" }
            inner.forEach {
                it.watcher.map { watcher -> watcher.close() }
            }
        })
    }


    override fun delete(domain: FileDomain): Option<FileSystemError> {
        val dir = basedir.resolve(domain.relativeRoot)
        return when (val res = Try { Files.delete(dir) }) {
            is Success -> None
            is Failure -> Some(FileSystemError.CannotDeleteFileError(dir, res.exception))
        }
    }

    override fun watchDirectory(
        path: Path,
        domain: FileCategory,
        op: (DirectoryChangeEvent) -> Unit
    ): WatchedDirectory {
        val shouldWatch = System.getProperty("itasserui.directory.watch") ?: true
        info { "Should watch $shouldWatch" }
        return when (shouldWatch) {
            false -> createUnwatchedDirectory(path, domain)
            else -> createWatchedDirectory(path, op, domain)
        }
    }

    private fun createUnwatchedDirectory(
        path: Path,
        category: FileCategory
    ) = WatchedDirectory(
        path = path,
        watcher = None,
        category = category
    ).also {
        inner += it
    }


    private fun createWatchedDirectory(
        path: Path,
        op: (DirectoryChangeEvent) -> Unit,
        category: FileCategory
    ) = DirectoryWatcher
        .builder()
        .path(path)
        .listener { event ->
            op(event)
            findByPath(event.path()).map { res -> res.update() }
        }.build()
        .let {
            WatchedDirectory(
                path = path,
                watcher = Some(it),
                category = category
            )
        }.also {
            inner += it
        }


    private fun findByPath(path: Path): Option<WatchedDirectory> {
        return inner.firstOrNone { dir ->
            dir.toRealPath() == path.toRealPath()
        }
    }
}