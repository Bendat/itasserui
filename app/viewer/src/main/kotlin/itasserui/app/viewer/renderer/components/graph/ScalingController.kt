package itasserui.app.viewer.renderer.components.graph

import javafx.beans.property.SimpleDoubleProperty
import tornadofx.Controller

class ScalingController: Controller() {
    val atoms = SimpleDoubleProperty(1.0)
    val bonds = SimpleDoubleProperty(1.0)
}