package itasserui.app.views.test

import itasserui.common.utils.uuid
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*

data class Stringer(val message: String)
class TestController(override val scope: Scope) : Controller() {
    val view: TestFragment by inject()
    val stringProperty: SimpleObjectProperty<Stringer> = Stringer("Hello").toProperty()
    var string by stringProperty

    val textProperty = SimpleObjectProperty<String>(string.message)
    var text by textProperty

    fun boundProperty(){

    }

}