@file:Suppress("MemberVisibilityCanBePrivate")

package itasserui.lib.filemanager


import arrow.core.None
import arrow.core.Option
import io.methvin.watcher.DirectoryWatcher
import itasserui.common.logger.Logger
import lk.kotlin.observable.property.StandardObservableProperty
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import kotlin.streams.toList

class WatchedDirectory(
    val path: Path,
    val category: FileDomain.FileCategory,
    val watcher: Option<DirectoryWatcher> = None
) : Path by path, Logger {
    val future = watcher.map { it.watchAsync() }
    val countProperty = StandardObservableProperty(0L)
    var count by countProperty
    val deepCountProperty = StandardObservableProperty(0L)
    var deepCount by deepCountProperty

    init {
        update()
    }

    fun update() {
        info{"Updating $this"}
        count = Files.list(path).count()
        deepCount = Files.walk(path).map {
            if (Files.isDirectory(it))
                Files.list(it)?.count()
            else null
        }.toList().mapNotNull { it }.sum()
    }

    override fun toString(): String {
        return "WatchedDirectory(path=${path.toAbsolutePath()}, category=$category, count=$count, deepCount=$deepCount)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WatchedDirectory

        if (path != other.path) return false
        if (category != other.category) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + category.hashCode()
        return result
    }
}