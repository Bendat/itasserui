@file:Suppress("unused")

package itasserui.lib.filemanager

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Try
import io.methvin.watcher.DirectoryChangeEvent
import itasserui.common.`typealias`.Outcome
import itasserui.common.logger.Logger
import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.list.filtering
import java.nio.file.Path


interface FileManager : Logger {
    val basedir: Path
    val inner: ObservableList<WatchedDirectory>
    val size get() = inner.size

    fun new(
        domain: FileDomain,
        new: Path,
        op: (DirectoryChangeEvent) -> Unit = {}
    ): Outcome<WatchedDirectory>

    fun watchDirectory(
        path: Path,
        category: FileDomain.FileCategory,
        op: (DirectoryChangeEvent) -> Unit
    ): WatchedDirectory

    fun wait(path: Path): Try<None>

    operator fun get(category: FileDomain.FileCategory) =
        inner.filtering { it.category == category }

    operator fun get(domain: FileDomain) =
        inner.filtering {
            it.toAbsolutePath()
                .startsWith(basedir.resolve(domain.directoryPath).toAbsolutePath())
        }

    fun new(
        domain: FileDomain,
        op: (DirectoryChangeEvent) -> Unit = {}
    ): Outcome<WatchedDirectory>

    fun delete(domain: FileDomain): Option<FileSystemError>
}