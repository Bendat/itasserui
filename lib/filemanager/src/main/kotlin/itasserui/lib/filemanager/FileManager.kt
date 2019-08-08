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
import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.list.filtering
import java.nio.file.Path


interface FileManager : Logger {
    val basedir: Path
    val inner: ObservableList<WatchedDirectory>
    val size get() = inner.size

    fun new(
        category: FileCategory,
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

    operator fun get(fileCategory: FileCategory, domain: FileDomain): Option<WatchedDirectory> =
        inner.firstOrNone {
            it.toAbsolutePath()
                .startsWith(fullPath(fileCategory, domain))
        }

    fun fullPath(fileCategory: FileCategory, domain: FileDomain): Path =
        basedir.resolve("$fileCategory/${domain.relativeRoot}").toAbsolutePath()

    fun exists(fileCategory: FileCategory, fileDomain: FileDomain): Boolean {
        return this[fileCategory, fileDomain].also{
            info{"Exist check is $it"}
        }.flatMap {
            FileSystem.Read
                .exists(fullPath(fileCategory, fileDomain).also{
                    info{"Exist check is $it"}
                })
                .toOption()
        }.fold({ false }) { it }
    }

    fun new(
        category: FileCategory,
        domain: FileDomain,
        op: (DirectoryChangeEvent) -> Unit = {}
    ): Outcome<WatchedDirectory>

    fun delete(domain: FileDomain): Option<FileSystemError>
}