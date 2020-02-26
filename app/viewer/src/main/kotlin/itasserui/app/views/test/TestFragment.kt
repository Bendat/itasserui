package itasserui.app.views.test

import itasserui.app.views.renderer.components.graph.GraphView
import itasserui.lib.pdb.parser.PDBParser
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.PerspectiveCamera
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import tornadofx.*
import java.nio.file.Paths


class TestFragment : Fragment(), ScopedInstance {
    class X
    class XModel:  ItemViewModel<X>()

    override val root = vbox {
        label("Graph")
    }
}