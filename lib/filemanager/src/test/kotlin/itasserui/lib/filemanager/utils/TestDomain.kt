package itasserui.lib.filemanager.utils

import itasserui.lib.filemanager.FileDomain
import itasserui.lib.filemanager.FileDomain.Subcategory
import java.util.*

data class TestDomain(
    override val category: FileDomain.FileCategory,
    override val relativeRootName: String,
    override val id: UUID,
    override val categories: List<Subcategory> = TestCategories.values().toList()
) : FileDomain

enum class TestCategories: Subcategory{
    First,
    Second,
    Third
}