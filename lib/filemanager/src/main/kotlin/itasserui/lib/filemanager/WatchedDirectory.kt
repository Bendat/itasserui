@file:Suppress("MemberVisibilityCanBePrivate")

package itasserui.lib.filemanager


import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import io.methvin.watcher.DirectoryWatcher
import itasserui.common.errors.RuntimeError
import itasserui.common.logger.Logger
import lk.kotlin.observable.property.StandardObservableProperty
import java.io.File
import java.net.URI
import java.nio.file.*
import kotlin.streams.toList

abstract class Directory : Path {
    abstract val rootDir: Path
    abstract val watcher: Option<DirectoryWatcher>
    abstract val unixPath: Path
    abstract val count: Long
    abstract val deepCount: Long
    val realPath get() = this.toAbsolutePath().toRealPath()

    override fun toFile(): File = unixPath.toFile()
    override fun isAbsolute(): Boolean = unixPath.isAbsolute
    override fun getFileName(): Path = unixPath.fileName
    override fun getName(index: Int): Path = unixPath.getName(index)
    override fun subpath(beginIndex: Int, endIndex: Int): Path = unixPath.subpath(beginIndex, endIndex)
    override fun endsWith(other: Path): Boolean = unixPath.endsWith(other)
    override fun endsWith(other: String): Boolean = unixPath.endsWith(other)
    override fun iterator(): MutableIterator<Path> = unixPath.iterator()
    override fun relativize(other: Path): Path = unixPath.relativize(other)
    override fun toUri(): URI = unixPath.toUri()
    override fun toRealPath(vararg options: LinkOption?): Path = unixPath.toRealPath()
    override fun normalize(): Path = unixPath.normalize()
    override fun getParent(): Path = unixPath.parent
    override fun compareTo(other: Path?): Int = unixPath.compareTo(other)
    override fun getNameCount(): Int = unixPath.nameCount
    override fun startsWith(other: Path): Boolean = unixPath.startsWith(other)
    override fun startsWith(other: String): Boolean = unixPath.startsWith(other)
    override fun getFileSystem(): FileSystem = unixPath.fileSystem
    override fun getRoot(): Path = unixPath.root
    override fun resolveSibling(other: Path): Path = unixPath.resolveSibling(other)
    override fun resolveSibling(other: String): Path = unixPath.resolveSibling(other)
    override fun resolve(other: Path): Path = unixPath.resolve(other)
    override fun resolve(other: String): Path = unixPath.resolve(other)
    override fun toAbsolutePath(): Path = unixPath.toAbsolutePath()
    override fun register(
        watcher: WatchService?,
        events: Array<out WatchEvent.Kind<*>>?,
        vararg modifiers: WatchEvent.Modifier?
    ): WatchKey = unixPath.register(watcher, events, *modifiers)

    override fun register(
        watcher: WatchService?,
        vararg events: WatchEvent.Kind<*>?
    ): WatchKey = unixPath.register(watcher, *events)

    override fun equals(other: Any?): Boolean {
        return unixPath.equals(other)
    }

    override fun hashCode(): Int {
        return unixPath.hashCode()
    }

    override fun toString(): String {
        return unixPath.toString()
    }
}

class DomainDirectory(
    override val rootDir: Path,
    val file: FileDomain,
    override val watcher: Option<DirectoryWatcher> = None
) : Directory(){
    override val unixPath get() = rootDir.resolve(file.relativeRoot)
    val countProperty = StandardObservableProperty(0L)
    override val count get() = countProperty.value
    val deepCountProperty = StandardObservableProperty(0L)
    override val deepCount get() = deepCountProperty.value
    val subdirectories = file.categories.map { SubdomainDirectory(unixPath, it, watcher) }
    val errors: List<RuntimeError> = arrayListOf()

    init {
        val errs = errors as MutableList
        Files.createDirectories(unixPath)
        subdirectories.map { FS.create.directories(it.unixPath) }
            .forEach { if (it is Either.Left) errs += it.a }
        countProperty.value = Files.list(unixPath).count()
        deepCountProperty.value = Files.walk(unixPath).map {
            if (Files.isDirectory(it))
                Files.list(it)?.count()
            else null
        }.toList()
            .mapNotNull { it }
            .sum()
    }

    operator fun get(category: FileDomain.Subcategory) =
        subdirectories.first { it.category == category }
}

class SubdomainDirectory(
    override val rootDir: Path,
    val category: FileDomain.Subcategory,
    override val watcher: Option<DirectoryWatcher>
) : Directory() {
    override val unixPath get() = rootDir.resolve(category.directory)
    val countProperty = StandardObservableProperty(0L)
    override val count get() = countProperty.value
    val deepCountProperty = StandardObservableProperty(0L)
    override val deepCount get() = deepCountProperty.value
}