package itasser.app.mytasser.lib.extensions

import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import lk.kotlin.observable.property.MutableObservableProperty
import lk.kotlin.observable.property.ObservableProperty
import lk.kotlin.observable.property.plusAssign

fun <T : Any> ObjectProperty<T>.bind(to: MutableObservableProperty<T>): ObjectProperty<T> {
    to += {
        Platform.runLater {
            this.value = it
        }
    }
    return this
}

fun <T> ObservableProperty<T>.bind(to: ObjectProperty<T>): ObservableProperty<T> {
    to.addListener { _, _, new ->
        to.value = new
    }
    return this
}
