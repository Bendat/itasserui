package itasserui.lib.filemanager.utils

import itasserui.lib.filemanager.FileDomain
import itasserui.lib.filemanager.FileDomain.Subcategory
import itasserui.lib.filemanager.WatchedDirectory
import lk.kotlin.observable.list.ObservableList
import java.util.*

data class TestDomain(
    override val category: FileDomain.FileCategory,
    override val relativeRootName: String,
    override var directories: ObservableList<WatchedDirectory>,
    override val id: UUID, override val categories: List<Subcategory> = listOf()
) : FileDomain