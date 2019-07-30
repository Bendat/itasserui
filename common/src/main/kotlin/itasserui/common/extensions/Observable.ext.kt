package itasserui.common.extensions

import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.property.ObservableProperty
import lk.kotlin.observable.property.plusAssign

fun <T, K> ObservableList<T>.addUpdatable(element: T, updateBy: (T) -> ObservableProperty<K>) {
    add(element).also {
        updateBy(element) += { update(element) }
    }
}