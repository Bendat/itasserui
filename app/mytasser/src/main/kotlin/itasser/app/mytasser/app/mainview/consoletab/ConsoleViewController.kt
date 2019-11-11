package itasser.app.mytasser.app.mainview.consoletab

import itasserui.lib.process.process.ITasser
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*

class SelectedSequenceEvent(val sequence: ITasser) : FXEvent()
class ConsoleViewController : Controller() {
    val selectedSequenceProperty = SimpleObjectProperty<ITasser?>()
    var selectedSequence by selectedSequenceProperty
    val std = SimpleObjectProperty(selectedSequence?.std?.err)
    init {
        subscribe<SelectedSequenceEvent> {
            selectedSequence = it.sequence
        }

        selectedSequenceProperty.onChange {

        }
    }
}