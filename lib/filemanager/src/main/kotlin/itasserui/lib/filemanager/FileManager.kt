package itasserui.lib.filemanager

import arrow.core.*
import io.methvin.watcher.DirectoryChangeEvent
import itasserui.common.`typealias`.Outcome
import itasserui.common.datastructures.observable.ObservableSet
import itasserui.common.logger.Logger
import itasserui.lib.filemanager.FileDomain.FileCategory
import itasserui.lib.filemanager.FileDomain.Subcategory
import lk.kotlin.observable.list.filtering
import java.nio.file.Files
import java.nio.file.Path


interface FileManager : Logger {
    val basedir: Path
    val inner: ObservableSet<WatchedDirectory>
    val size get() = inner.size

    fun watchDirectory(
        path: Path,
        domain: FileDomain,
        op: (DirectoryChangeEvent) -> Unit
    ): WatchedDirectory

    fun wait(path: Path): Try<None>

    operator fun get(category: FileCategory) =
        inner.filtering { it.file.category == category }

    operator fun get(domain: FileDomain): Option<WatchedDirectory> =
        inner.firstOrNone {
            it.toAbsolutePath()
                .startsWith(fullPath(domain))
        }.map { it.update(); it }


    operator fun get(
        domain: FileDomain,
        path: List<Subcategory>
    ): List<Path> = FS.create[fullPath(domain), path.toList().also { info { "Paths are ${it}" } }]

    fun mkdirs(
        domain: FileDomain,
        vararg path: Path
    ): List<Path> = inner
        .firstOrNone { directory -> directory.file == domain }
        .map { directory -> resolvePaths(path, directory) }
        .map { paths ->
            paths.map { path -> Files.createDirectories(path) }
        }.getOrElse { listOf() }

    fun fullPath(domain: FileDomain): Path =
        basedir.resolve("${domain.relativeRoot}").toAbsolutePath()

    fun exists(fileDomain: FileDomain): Boolean = this[fileDomain]
        .flatMap {
            FS.Read
                .exists(fullPath(fileDomain))
                .toOption()
        }.fold(
            { false },
            { it }
        )

    fun new(
        domain: FileDomain,
        op: (DirectoryChangeEvent) -> Unit = {}
    ): Outcome<WatchedDirectory>

    private fun resolveDomain(
        file: WatchedDirectory,
        path: Path
    ): Path = fullPath(file.file).resolve(path)

    fun resolvePaths(
        path: Array<out Path>,
        file: WatchedDirectory
    ): List<Path> = path
        .map { domainPath -> resolveDomain(file, domainPath) }
        .mapNotNull { FS.create.directories(it).getOrElse { null } }

    fun delete(domain: FileDomain): Option<FileSystemError>
}