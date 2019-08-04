@file:Suppress("unused")

package itasserui.lib.filemanager

import com.fasterxml.jackson.annotation.JsonIgnore
import itasserui.common.interfaces.Identifiable
import lk.kotlin.observable.list.ObservableList
import java.nio.file.Path
import java.nio.file.Paths

interface FileDomain : Identifiable {
    @get:JsonIgnore
    val category: FileCategory
    @get:JsonIgnore
    val directoryName: String
    @get:JsonIgnore
    val directoryPath: Path
        get() = Paths.get("$category/$directoryName")
    @get:JsonIgnore
    var directories: ObservableList<WatchedDirectory>

    enum class FileCategory {
        Users,
        Settings,
        Test,
        Database;

        override fun toString(): String {
            return name.toLowerCase()
        }
    }

    interface Subcategory{
        val directory: Path
    }
}