@file:Suppress("MemberVisibilityCanBePrivate")

package itasserui.lib.filemanager


import io.methvin.watcher.DirectoryWatcher
import lk.kotlin.observable.property.StandardObservableProperty
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import kotlin.streams.toList

class WatchedDirectory(
    path: Path,
    val category: FileDomain.FileCategory,
    val watcher: DirectoryWatcher,
    val future: CompletableFuture<Void>
) : Path by path {
    val countProperty = StandardObservableProperty(0L)
    var count by countProperty
    val deepCountProperty = StandardObservableProperty(0L)
    var deepCount by deepCountProperty

    init {
        update()
    }

    fun update() {
        count = Files.list(this).count()
        deepCount = Files.walk(this).map {
            if (Files.isDirectory(it))
                Files.list(it)?.count()
            else null
        }.toList().mapNotNull { it }.sum()
    }

    override fun toString(): String {
        return "WatchedDirectory(path=${super.toString()}, category=$category, count=$count, deepCount=$deepCount)"
    }
}