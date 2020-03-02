package itasserui.app.viewer.renderer.components.canvas

import itasserui.app.viewer.renderer.components.graph.GraphView
import itasserui.app.viewer.ui.ViewerController
import javafx.geometry.Point3D
import javafx.scene.PerspectiveCamera
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.layout.StackPane
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import tornadofx.View
import tornadofx.stackpane
import kotlin.math.pow
import kotlin.math.sqrt


class CanvasView : View() {
    private val graph: GraphView by inject()
    private val controller: ViewerController by inject()
    override val root = stackpane {
        canvasScrollHandlers()
        val perspectiveCamera = PerspectiveCamera(true)
        val subScene = SubScene(graph.root, 500.0, 500.0, true, SceneAntialiasing.BALANCED)
        children.add(subScene)
        subScene.widthProperty().bind(widthProperty())
        subScene.heightProperty().bind(heightProperty())
        perspectiveCamera.nearClip = 0.1
        perspectiveCamera.farClip = PaneDepth * 2
        perspectiveCamera.translateZ = -PaneHeight / 2
        subScene.camera = perspectiveCamera
    }

    companion object {
        const val PaneDepth = 10000.0
        const val PaneHeight = 600.0
        const val PaneWidth = PaneHeight
    }

    private fun StackPane.canvasScrollHandlers() {
        setOnMousePressed { event ->
            controller.pressedX = event.sceneX
            controller.pressedY = event.sceneY
            event.consume()
        }

        setOnMouseDragged { event ->
            val deltaX = event.sceneX - controller.pressedX
            val deltaY = event.sceneY - controller.pressedY

            // Get the perpendicular axis for the dragged point
            val direction = Point3D(deltaX, deltaY, 0.0)
            val axis = direction.crossProduct(0.0, 0.0, 1.0)
            val angle = 0.4 * sqrt(deltaX.pow(2.0) + deltaY.pow(2.0))

            //compute the main focus of the world and use it as pivot
            val focus = graph.controller.computePivot()

            val rotation = Rotate(angle, focus.x, focus.y, focus.z, axis)

            // Apply the rotation as an additional transform, keeping earlier modifications
            graph.controller.worldTransform =
                rotation.createConcatenation(graph.controller.worldTransform)

            // Set the variables new
            controller.pressedX = event.sceneX
            controller.pressedY = event.sceneY
            event.consume()
        }

        setOnScroll { event ->
            val delta = 0.01 * event.deltaY + 1
            val focus = graph.controller.computePivot()
            val scale = Scale(delta, delta, delta, focus.x, focus.y, focus.z)
            val value = scale.createConcatenation(graph.controller.worldTransform)
            graph.controller.worldTransformProperty.value = value

        }
    }
}