@file:Suppress("unused")

package itasserui.lib.filemanager

import arrow.core.*
import io.methvin.watcher.DirectoryChangeEvent
import io.methvin.watcher.DirectoryWatcher
import itasserui.common.`typealias`.Outcome
import itasserui.common.datastructures.observable.ObservableSet
import itasserui.common.datastructures.observable.observableSetOf
import itasserui.lib.filemanager.FileDomain.Subcategory
import java.nio.file.Files
import java.nio.file.Path


class LocalFileManager(
    override val basedir: Path,
    override val inner: ObservableSet<WatchedDirectory> = observableSetOf()
) : FileManager {
    override fun getDirectories(domain: FileDomain): Map<Subcategory, WatchedDirectory> {
        val dir = fullPath(domain)
        return domain
            .categories
            .map { it to dir.resolve(it.directory) }
            .filter { Files.exists(it.second) }
            .map { it.first to watchDirectory(it.second, domain) }
            .toMap()
    }

    override fun new(
        domain: FileDomain,
        op: (DirectoryChangeEvent) -> Unit
    ): Outcome<WatchedDirectory> {
        val dir = fullPath(domain)
        return when (val res = FS.Create.directories(dir)) {
            is Either.Left -> res
            is Either.Right -> watchDirectory(dir, domain, op).right()
        }
    }

    override fun wait(path: Path): Try<None> = Try {
        findByPath(path).flatMap {
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
        domain: FileDomain,
        op: (DirectoryChangeEvent) -> Unit
    ): WatchedDirectory {
        val shouldWatch = System.getProperty("itasserui.directory.watch") ?: true
        info { "Should watch $shouldWatch" }
        return when (shouldWatch) {
            false -> createWatchedDirectory(path, domain, op)
            else -> createWatchedDirectory(path, domain, op)
        }
    }

    private fun createUnwatchedDirectory(
        file: FileDomain
    ) = WatchedDirectory(
        rootDir = basedir,
        file = file,
        watcher = None
    ).also {
        inner += it
    }


    private fun createWatchedDirectory(
        path: Path,
        domain: FileDomain,
        op: (DirectoryChangeEvent) -> Unit
    ) = DirectoryWatcher
        .builder()
        .path(path)
        .listener { event ->
            op(event)
            findByPath(event.path())
                .map { res: WatchedDirectory -> res.update() }
        }.build()
        .let {
            WatchedDirectory(
                rootDir = basedir,
                file = domain,
                watcher = Some(it)
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