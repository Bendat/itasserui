@file:Suppress("unused")

package itasserui.lib.filemanager

import arrow.core.None
import arrow.core.Option
import arrow.core.Try
import arrow.core.firstOrNone
import io.methvin.watcher.DirectoryChangeEvent
import itasserui.common.`typealias`.Outcome
import itasserui.common.logger.Logger
import itasserui.lib.filemanager.FileDomain.FileCategory
import itasserui.lib.filemanager.FileDomain.Subcategory
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
        domain: FileCategory,
        op: (DirectoryChangeEvent) -> Unit
    ): WatchedDirectory

    fun wait(path: Path): Try<None>

    operator fun get(category: FileCategory) =
        inner.filtering { it.category == category }

    operator fun get(domain: FileDomain): Option<WatchedDirectory> =
        inner.firstOrNone {
            it.toAbsolutePath().startsWith(fullPath(domain))
        }

    @Suppress("ReplaceGetOrSet")
    operator fun get(
        domain: FileDomain,
        path: List<Subcategory>
    ): List<Path> =
        FileSystem.Create.get(fullPath(domain), *path.toTypedArray())

    fun fullPath(domain: FileDomain): Path =
        basedir.resolve("${domain.relativeRoot}").toAbsolutePath()

    fun exists(fileDomain: FileDomain): Boolean {
        return this[fileDomain].also {
            info { "Exist check is $it" }
        }.flatMap {
            FileSystem.Read
                .exists(fullPath(fileDomain).also {
                    info { "Exist check is $it" }
                }).toOption()
        }.fold({ false }) { it }
    }

    fun new(
        domain: FileDomain,
        op: (DirectoryChangeEvent) -> Unit = {}
    ): Outcome<WatchedDirectory>

    fun delete(domain: FileDomain): Option<FileSystemError>
}