package itasserui.app.views.renderer.components.canvas

import itasserui.app.events.PDBLoadedEvent
import itasserui.app.views.renderer.components.graph.GraphView
import javafx.event.EventTarget
import javafx.scene.PerspectiveCamera
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.layout.StackPane
import tornadofx.View
import tornadofx.opcr
import tornadofx.pane
import tornadofx.stackpane

fun EventTarget.canvas3d(op: StackPane.(CanvasView) -> Unit = {}): StackPane {
    val view = CanvasView()
    return opcr(this, view.root, { op(view) })
}

class CanvasView : View() {
    val graph: GraphView by inject()

    init {
        subscribe<PDBLoadedEvent> {
            graph.pdb = it.pdb
        }
    }

    override val root = stackpane {
        prefHeight = 300.0
        pane {
            val subScene = SubScene(graph.root, 500.0, 500.0, true, SceneAntialiasing.BALANCED)
            children.add(subScene)
            subScene.widthProperty().bind(widthProperty())
            subScene.heightProperty().bind(heightProperty())
            val perspectiveCamera = PerspectiveCamera(true)
            perspectiveCamera.nearClip = 0.1
            perspectiveCamera.farClip = PaneDepth * 2
            perspectiveCamera.translateZ = -PaneHeight / 2
            subScene.camera = perspectiveCamera
        }
    }

    companion object {
        const val PaneDepth = 10000.0
        const val PaneHeight = 600.0
    }
}