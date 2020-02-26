package itasserui.app.views.test

import tornadofx.Controller
import tornadofx.ItemViewModel
import tornadofx.Scope
import tornadofx.onChange

abstract class InjectModel<T: Controller>(item: T): ItemViewModel<T>(){
    init{
        itemProperty.onChange { setInScope(item, scope) }
        super.item = item
    }
}
class TestModel : ItemViewModel<TestController>() {
    val text = bind(TestController::textProperty)
}