package itasser.app.mytasser.lib.extensions

import itasserui.common.extensions.unless
import itasserui.lib.process.process.ITasser
import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import lk.kotlin.observable.property.MutableObservableProperty
import lk.kotlin.observable.property.ObservableProperty
import lk.kotlin.observable.property.plusAssign
import tornadofx.move
import tornadofx.onChange
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
        Platform.runLater {
            { this.add(index, item) } unless contains(item)
        }
    }

    to.onRemove += { item, _ ->
        Platform.runLater { this.remove(item) }
    }

    to.onChange += { item1, item, index ->
        Platform.runLater { this.move(item, index) }
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

fun <T> MutableObservableProperty<T>.toFx(): Property<T> {
    val prop = SimpleObjectProperty(this.value)
    this += {
        prop.value = it
    }

    prop.onChange {
        it?.let{ value = it }
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