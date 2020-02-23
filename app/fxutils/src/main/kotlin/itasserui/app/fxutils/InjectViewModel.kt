package itasserui.app.fxutils

import tornadofx.Controller
import tornadofx.ItemViewModel

abstract class InjectViewModel<T : Controller>(item: T) : ItemViewModel<T>(item) {
    init {
        setInScope(this)
        setInScope(item)
    }
}