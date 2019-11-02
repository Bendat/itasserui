package itasserui.app.mytasser.lib.extensions

import itasserui.lib.process.process.ITasser
import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import lk.kotlin.observable.property.MutableObservableProperty
import lk.kotlin.observable.property.ObservableProperty
import lk.kotlin.observable.property.plusAssign
import tornadofx.move
import javafx.collections.ObservableList as FXList
import lk.kotlin.observable.list.ObservableList as LKList

fun <T : Any> ObjectProperty<T>.bind(to: MutableObservableProperty<T>): ObjectProperty<T> {
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
