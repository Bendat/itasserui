package itasserui.app.viewer.footer

import itasserui.app.viewer.ui.components.CountView
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*

abstract class FooterComponent: View() {
    class EmptyFooter: FooterComponent(){
        override val root = pane{}
    }
}


class FooterController : Controller() {
    val currentViewProperty = SimpleObjectProperty<FooterComponent>(FooterComponent.EmptyFooter())
    var currentView by currentViewProperty
}

class FooterView : View() {
    val controller: FooterController by inject()
    override val root = pane {
        maxHeight = 0.0
        controller.currentViewProperty.onChange {
            children.clear()
            children.add(it?.root)
        }
    }
}