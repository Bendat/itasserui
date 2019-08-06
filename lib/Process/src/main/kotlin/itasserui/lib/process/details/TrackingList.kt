package itasserui.lib.process.details

import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.list.ObservableListWrapper
import lk.kotlin.observable.property.ObservableProperty
import org.joda.time.DateTime

class TrackingList<T> : ObservableList<TrackedItem<T>> {
    private val inner = ObservableListWrapper<TrackedItem<T>>()
    override fun add(element: TrackedItem<T>) =
        inner.add(element)

    override fun add(index: Int, element: TrackedItem<T>) =
        inner.add(index, element)

    override val onAdd: MutableCollection<(TrackedItem<T>, Int) -> Unit>
        get() = inner.onAdd
    override val onChange: MutableCollection<(TrackedItem<T>, TrackedItem<T>, Int) -> Unit>
        get() = inner.onChange
    override val onMove: MutableCollection<(TrackedItem<T>, Int, Int) -> Unit>
        get() = inner.onMove
    override val onRemove: MutableCollection<(TrackedItem<T>, Int) -> Unit>
        get() = inner.onRemove
    override val onReplace: MutableCollection<(ObservableList<TrackedItem<T>>) -> Unit>
        get() = inner.onReplace
    override val onUpdate: ObservableProperty<ObservableList<TrackedItem<T>>>
        get() = inner.onUpdate
    override val size: Int
        get() = inner.size

    override fun contains(element: TrackedItem<T>) =
        inner.contains(element)

    override fun containsAll(elements: Collection<TrackedItem<T>>) =
        inner.containsAll(elements)

    override fun get(index: Int) = inner[index]
    override fun indexOf(element: TrackedItem<T>) = inner.indexOf(element)
    override fun isEmpty() = inner.isEmpty()
    override fun iterator(): MutableIterator<TrackedItem<T>> = inner.iterator()

    override fun lastIndexOf(element: TrackedItem<T>) =
        inner.lastIndexOf(element)

    override fun addAll(index: Int, elements: Collection<TrackedItem<T>>) =
        inner.addAll(index, elements)

    override fun addAll(elements: Collection<TrackedItem<T>>) =
        inner.addAll(elements)

    override fun clear() =
        inner.clear()

    override fun listIterator(): MutableListIterator<TrackedItem<T>> =
        inner.listIterator()

    override fun listIterator(index: Int): MutableListIterator<TrackedItem<T>> =
        inner.listIterator(index)

    override fun move(fromIndex: Int, toIndex: Int) =
        inner.move(fromIndex, toIndex)

    override fun remove(element: TrackedItem<T>) =
        inner.remove(element)

    override fun removeAll(elements: Collection<TrackedItem<T>>) =
        inner.removeAll(elements)

    override fun removeAt(index: Int): TrackedItem<T> =
        inner.removeAt(index)

    override fun replace(list: List<TrackedItem<T>>) =
        inner.replace(list)

    override fun retainAll(elements: Collection<TrackedItem<T>>) =
        inner.retainAll(elements)

    override fun set(index: Int, element: TrackedItem<T>): TrackedItem<T> =
        inner.set(index, element)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<TrackedItem<T>> =
        inner.subList(fromIndex, toIndex)

    @Suppress("MemberVisibilityCanBePrivate")
    fun add(item: T) {
        add(TrackedItem(item))
    }

    operator fun plusAssign(item: T) = add(item)

    override fun toString(): String {
        val str = joinToString(separator = ", ")
        return "TrackingList{$str})"
    }

}

data class TrackedItem<T>(
    val item: T,
    val timestamp: DateTime = DateTime.now()
) {
    val line get() = "[$timestamp]> $item"
    override fun toString(): String {
        return "{$timestamp: $item}"
    }
}