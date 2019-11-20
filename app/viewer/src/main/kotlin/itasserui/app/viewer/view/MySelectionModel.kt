package itasserui.app.viewer.view

import itasserui.common.logger.Logger
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener
import javafx.scene.control.MultipleSelectionModel
import javafx.scene.control.SelectionMode


/**
 * Selection model
 * Created by huson on 12/15/15.
 */
class MySelectionModel<T>(vararg items: T) : MultipleSelectionModel<T>(), Logger {

    val selectedIndices: ObservableSet<Int> = FXCollections.observableSet()

    var items: List<T> = items.toList()
        set(value) {
            clearSelection()
            field = value.toList()// use copy for safety
        }// need a copy of this array to map indices to objects, when required

    var focusIndex = -1 // focus index

    var unmodifiableSelectedIndices: ObservableList<Int> = FXCollections.observableArrayList<Int>()
    var unmodifiableSelectedItems: ObservableList<T> = FXCollections.observableArrayList<T>()

    init {
        info { "Initial items are ${this.items}" }
        selectionModeProperty().value = SelectionMode.MULTIPLE

        // setup unmodifiable lists
        run {
            val selectedIndicesAsList = FXCollections.observableArrayList<Int>()
            val selectedItems = FXCollections.observableArrayList<T>()

            // first setup observable array lists that listen for changes of the selectedIndices set
            selectedIndices.addListener(SetChangeListener<Int> { c ->
                if (c.wasAdded()) {
                    selectedIndicesAsList.add(c.elementAdded)
                    info { "Items are ${this.items}" }
                    selectedItems.add(this.items[c.elementAdded])

                } else if (c.wasRemoved()) {
                    selectedIndicesAsList.remove(c.elementRemoved)
                    selectedItems.remove(this.items[c.elementRemoved])
                }
            } as SetChangeListener<Int>)
            // wrap a unmodifiable observable list around the observable arrays lists
            unmodifiableSelectedIndices = FXCollections.unmodifiableObservableList(selectedIndicesAsList)
            unmodifiableSelectedItems = FXCollections.unmodifiableObservableList(selectedItems)
        }
    }

    override fun getSelectedIndices(): ObservableList<Int> {
        return unmodifiableSelectedIndices
    }

    override fun getSelectedItems(): ObservableList<T> {
        return unmodifiableSelectedItems
    }

    override fun selectIndices(index: Int, vararg indices: Int) {
        select(index)
        for (i in indices) {
            select(i)
        }
    }

    override fun selectAll() {
        for (index in items.indices) {
            selectedIndices.add(index)
        }
        focusIndex = -1
    }

    override fun clearAndSelect(index: Int) {
        clearSelection()
        select(index)
    }

    fun clearAndSelect(item: T) {
        clearSelection()
        select(item)
    }

    override fun select(index: Int) {
        if (index >= 0 && index < items.size) {
            selectedIndices.add(index)
            focusIndex = index
        }
    }

    override fun select(item: T) {
        for (i in items.indices) {
            if (items[i] == item) {
                select(i)
                return
            }
        }
    }

    override fun clearSelection(index: Int) {
        if (index >= 0 && index < items.size) {
            selectedIndices.remove(index)
        }
    }

    fun clearSelection(item: T) {
        for (i in items!!.indices) {
            if (items!![i] == item) {
                clearSelection(i)
                break
            }
        }
    }

    override fun clearSelection() {
        selectedIndices.clear()
        focusIndex = -1
    }

    override fun isSelected(index: Int): Boolean {
        return index >= 0 && index < items!!.size && selectedIndices.contains(index)
    }

    fun isSelected(item: T): Boolean {
        val index = -1
        for (i in items!!.indices) {
            if (items!![i] == item) {
                return isSelected(i)
            }
        }
        return false
    }

    override fun isEmpty(): Boolean {
        return selectedIndices.isEmpty()
    }

    override fun selectFirst() {
        if (items!!.size > 0) {
            select(0)
        }
    }

    override fun selectLast() {
        if (items!!.size > 0) {
            select(items!!.size - 1)
        }
    }

    override fun selectPrevious() {
        select(focusIndex - 1)
    }

    override fun selectNext() {
        select(focusIndex + 1)
    }


}
