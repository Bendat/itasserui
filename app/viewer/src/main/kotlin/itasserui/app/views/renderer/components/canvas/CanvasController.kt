package itasserui.app.views.renderer.components.canvas

import itasserui.app.viewer.pdbmodel.Atom
import itasserui.app.viewer.pdbmodel.Bond
import itasserui.app.views.renderer.components.node.NodeView
import itasserui.app.views.renderer.data.edge.EdgeView
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.Group
import tornadofx.getValue

class CanvasController {
    val nodeViewGroupProperty = SimpleObjectProperty(Group())
    val nodeViewGroup by nodeViewGroupProperty

    val residueViewGroupProperty = SimpleObjectProperty(Group())
    val residueViewGroup by residueViewGroupProperty

    val edgeViewGroupProperty = SimpleObjectProperty(Group())
    val edgeViewGroup by edgeViewGroupProperty

    val secondaryViewGroupProperty = SimpleObjectProperty(Group())
    val secondaryViewGroup by secondaryViewGroupProperty

    val atomViews = FXCollections.observableHashMap<Atom, NodeView>()
    val edgeViews = FXCollections.observableHashMap<Bond, EdgeView>()
}