@file:Suppress("unused")

package itasserui.lib.filemanager

import com.fasterxml.jackson.annotation.JsonIgnore
import itasserui.common.interfaces.Identifiable
import java.nio.file.Path

interface FileDomain : Identifiable {
    @get:JsonIgnore
    val category: FileCategory
    @get:JsonIgnore
    val relativeRootName: String
    @get:JsonIgnore
    val relativeRoot: Path
        get() = FS["$category/$relativeRootName"]

    val categories: List<Subcategory>

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
        val name: String
        val directory: Path
            get() =
                FS[name.toLowerCase()]
    }
}