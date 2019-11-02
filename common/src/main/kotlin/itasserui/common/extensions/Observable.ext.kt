package itasserui.common.extensions

import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.property.ObservableProperty
import lk.kotlin.observable.property.plusAssign

fun <T, K> ObservableList<T>.addUpdatableProperty(element: T, updateBy: (T) -> ObservableProperty<K>):
        ObservableList<T> {
    add(element).also {
        updateBy(element) += {
            println("Updating element $element")
            update(element)
        }
    }
    return this

}

fun <T> ObservableList<T>.addUpdatable(element: T, updateBy: (T) -> List<ObservableProperty<*>>): ObservableList<T> {
    add(element).also {
        updateBy(element).forEach {
            it += {
                update(element)
            }
        }
    }
    return this
}