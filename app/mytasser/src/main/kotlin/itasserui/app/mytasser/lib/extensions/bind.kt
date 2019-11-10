package itasserui.app.mytasser.lib.extensions

import itasserui.lib.process.process.ITasser
import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.collections.FXCollections
import lk.kotlin.observable.property.ObservableProperty
import lk.kotlin.observable.property.StandardObservableProperty
import lk.kotlin.observable.property.plusAssign
import tornadofx.move
import javafx.collections.ObservableList as FXList
import lk.kotlin.observable.list.ObservableList as LKList

fun <T : Any> ObjectProperty<T>.bind(to: ObservableProperty<T>): ObjectProperty<T> {
    to += {
        Platform.runLater {
            this.value = it
        }
    }
    return this
}

fun <T : Any> FXList<T>.bind(to: LKList<T>): FXList<T> {
    to.onAdd += { item, index ->
        this.add(index, item)
    }

    to.onRemove += { item, _ ->
        this.remove(item)
    }

    to.onChange += { item1, item, index ->
        println("Moving items $index $item1, $item")
        this.move(item, index)
    }
    return this
}

fun <T : Any> LKList<T>.toFX() = FXCollections.observableArrayList(this.toList()).bind(this)

fun <T : Any, K : Any> LKList<T>.bindProperty(prop: (T) -> ObservableProperty<K>): LKList<T> {
    forEach {
        prop(it) += { _ ->
            update(it)
        }
    }
    return this
}

fun LKList<ITasser>.bindPriority(): LKList<ITasser> =
    bindProperty { it.priorityProperty }

fun <T> ObservableProperty<T>.bind(to: ObjectProperty<T>): ObservableProperty<T> {
    to.addListener { _, _, new ->
        to.value = new
    }
    return this
}

fun <T> ObservableProperty<T>.toFx(): StandardObservableProperty<T> {
    val prop = StandardObservableProperty(this.value)
    this += {
        prop.value = it
    }

    return prop
}

fun <T, K> FXList<T>.bind(to: LKList<K>, mapping: (K) -> T): FXList<T> {
    addAll(to.map { mapping(it) })
    to.onAdd += { item, idx ->
        println("List update even $idx")
        add(idx, mapping(item))
    }

    to.onMove += { item, _, newIdx ->
        move(mapping(item), newIdx)
    }

    to.onRemove += { item, idx ->
        remove(mapping(item))
    }
    return this
}