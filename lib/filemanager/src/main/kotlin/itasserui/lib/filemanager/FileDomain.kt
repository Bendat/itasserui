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
    val relativeRootName: String
    @get:JsonIgnore
    val relativeRoot: Path
        get() = Paths.get("$category/$relativeRootName")

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

    interface Subcategory {
        val directory: Path
    }
}