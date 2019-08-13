@file:Suppress("unused")

package itasserui.lib.filemanager

import com.fasterxml.jackson.annotation.JsonIgnore
import itasserui.common.interfaces.Identifiable
import lk.kotlin.observable.list.ObservableList
import java.nio.file.Path

interface FileDomain : Identifiable {
    @get:JsonIgnore
    val category: FileCategory
    @get:JsonIgnore
    val relativeRootName: String
    @get:JsonIgnore
    val relativeRoot: Path
        get() = FileSystem["$category/$relativeRootName"]

    val categories: List<Subcategory>
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
        val directory: Path get() =
            FileSystem[javaClass.simpleName.toLowerCase()]
    }
}