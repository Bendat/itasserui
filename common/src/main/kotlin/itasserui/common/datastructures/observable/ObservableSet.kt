package itasserui.common.datastructures.observable

import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.list.ObservableListWrapper
import lk.kotlin.observable.property.ObservableProperty
import java.util.*

interface ObservableSet<T> : Set<T>, ObservableList<T> {
    val inner: ObservableList<T>
    override fun spliterator(): Spliterator<T> =
        inner.spliterator()
}

fun <T> observableSetOf(vararg items: T) =
    ObservableSetWrapper(items.toMutableList())

class ObservableSetWrapper<T>(items: MutableList<T> = mutableListOf()) : ObservableSet<T> {
    override val inner = ObservableListWrapper(items)
    override val onAdd: MutableCollection<(T, Int) -> Unit>
        get() = inner.onAdd
    override val onChange: MutableCollection<(T, T, Int) -> Unit>
        get() = inner.onChange
    override val onMove: MutableCollection<(T, Int, Int) -> Unit>
        get() = inner.onMove
    override val onRemove: MutableCollection<(T, Int) -> Unit>
        get() = inner.onRemove
    override val onReplace: MutableCollection<(ObservableList<T>) -> Unit>
        get() = inner.onReplace
    override val onUpdate: ObservableProperty<ObservableList<T>>
        get() = inner.onUpdate
    override val size: Int
        get() = inner.size

    override fun contains(element: T): Boolean =
        inner.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean =
        inner.containsAll(elements)

    override fun get(index: Int): T =
        inner[index]

    override fun indexOf(element: T): Int =
        inner.indexOf(element)

    override fun isEmpty(): Boolean =
        inner.isEmpty()

    override fun iterator(): MutableIterator<T> =
        inner.iterator()

    override fun lastIndexOf(element: T): Int =
        inner.lastIndexOf(element)

    override fun add(element: T): Boolean =
        if (inner.contains(element)) false
        else inner.add(element)

    override fun add(index: Int, element: T) {
        if (!inner.contains(element))
            inner.add(index, element)
        else inner.remove(element)
            .also { inner.add(index, element) }
    }


    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        inner.removeAll(elements)
        return inner.addAll(index, elements)
    }

    override fun addAll(elements: Collection<T>): Boolean =
        elements
            .filter { !inner.contains(it) }
            .map { inner.add(it) }
            .reduce { first, second -> first and second }

    override fun clear() {
        inner.clear()
    }

    override fun listIterator(): MutableListIterator<T> =
        inner.listIterator()

    override fun listIterator(index: Int): MutableListIterator<T> =
        inner.listIterator(index)

    override fun move(fromIndex: Int, toIndex: Int) =
        inner.move(fromIndex, toIndex)

    override fun remove(element: T): Boolean =
        inner.remove(element)

    override fun removeAll(elements: Collection<T>): Boolean =
        inner.removeAll(elements)

    override fun removeAt(index: Int): T =
        inner.removeAt(index)

    override fun replace(list: List<T>) =
        inner.replace(list)

    override fun retainAll(elements: Collection<T>): Boolean =
        inner.retainAll(elements)

    override fun set(index: Int, element: T): T =
        inner.set(index, element)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
        inner.subList(fromIndex, toIndex)
}