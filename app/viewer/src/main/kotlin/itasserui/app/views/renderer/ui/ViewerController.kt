package itasserui.app.views.renderer.ui

import itasserui.app.views.renderer.components.graph.GraphController
import javafx.beans.property.SimpleDoubleProperty
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue
import tornadofx.toProperty

class ViewerController: Controller() {
    val graph: GraphController by inject()
    val edgeScale = SimpleDoubleProperty(1.0)
    val nodesScale = SimpleDoubleProperty(1.0)
    val pressedXProperty = 0.0.toProperty()
    var pressedX by pressedXProperty
    val pressedYProperty = 0.0.toProperty()
    var pressedY by pressedYProperty
}