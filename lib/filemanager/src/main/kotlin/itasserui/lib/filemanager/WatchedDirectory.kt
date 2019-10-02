@file:Suppress("MemberVisibilityCanBePrivate")

package itasserui.lib.filemanager


import arrow.core.None
import arrow.core.Option
import io.methvin.watcher.DirectoryWatcher
import itasserui.common.logger.Logger
import lk.kotlin.observable.property.StandardObservableProperty
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

interface Directory {
    val rootDir: Path
    val file: FileDomain
    val watcher: Option<DirectoryWatcher>
    val path: Path
    val realPath: Path
    var count: Long
    var deepCount: Long
}

class WatchedDirectory(
    override val rootDir: Path,
    override val file: FileDomain,
    override val watcher: Option<DirectoryWatcher> = None
) : Path by rootDir.resolve(file.relativeRoot), Logger, Directory {
    override val path: Path get() = rootDir.resolve(file.relativeRoot)
    override val realPath: Path get() = path.toAbsolutePath().toRealPath()
    val countProperty = StandardObservableProperty(0L)
    override var count: Long by countProperty
    val deepCountProperty = StandardObservableProperty(0L)
    override var deepCount by deepCountProperty

    init {
        update()
        watcher.map { it.watchAsync() }
    }

    fun update(num: Int = 0) {
        info { "Updating $num $path[${Files.list(path).toList()}]" }
        count = Files.list(path).count()
        deepCount = Files.walk(path).map {
            if (Files.isDirectory(it))
                Files.list(it)
                    ?.count()
            else null
        }.toList()
            .mapNotNull { it }
            .sum()
    }

    override fun toString(): String {
        return "WatchedDirectory(path=${path.toAbsolutePath()}, category=${file.category}, count=$count, deepCount=$deepCount)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WatchedDirectory

        if (path != other.path) return false
        if (file.category != other.file.category) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + file.category.hashCode()
        return result
    }
//     TODO: make these return directry watchers

//    override fun normalize(): Path {
//        return
//    }
//
//    override fun resolve(other: String): Path {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun resolve(other: Path): Path {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun toAbsolutePath(): Path {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
}