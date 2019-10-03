package itasser.app.mytasser.lib.extensions

import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import lk.kotlin.observable.property.MutableObservableProperty
import lk.kotlin.observable.property.ObservableProperty
import lk.kotlin.observable.property.plusAssign

fun <T : Any> ObjectProperty<T>.bind(to: MutableObservableProperty<T>): ObjectProperty<T> {
    to += {
        Platform.runLater {
            println("Updating property from fx with value $it")
            println(this.bean)
            this.value = it
            println("Which makes this ${this.value}")
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
