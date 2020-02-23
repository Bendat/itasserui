package itasserui.app.views.renderer.components.box2d

import itasserui.app.views.renderer.components.node.NodeView
import javafx.beans.binding.DoubleBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.Property
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.View
import tornadofx.group

class Box2DView(
    val parentPane: Pane,
    val node: Node,
    val nodeView: NodeView,
    transform: Property<*>
) : View("My View") {
    val boxProperties: Array<Property<*>> = arrayOf(transform, *nodeView.viewProperties)

    override val root = group {
        val binding = createBoundingBox(parentPane, node, boxProperties)
        val box = Rectangle()
        children += box
        isPickOnBounds = false
        box.stroke = Color.CORNFLOWERBLUE
        box.fill = Color(0.39215687, 0.58431375, 0.92941177, 0.3)
        box.xProperty().bind(doubleBinding(binding) { it.x })
        box.yProperty().bind(doubleBinding(binding) { it.y })
        box.scaleXProperty().bind(doubleBinding(binding) { it.scaleX })
        box.scaleYProperty().bind(doubleBinding(binding) { it.scaleY })
        box.scaleZProperty().bind(doubleBinding(binding) { it.scaleZ })
        box.heightProperty().bind(doubleBinding(binding) { it.height })
        box.widthProperty().bind(doubleBinding(binding) { it.width })
    }

    private fun doubleBinding(
        binding: ObjectBinding<Rectangle>,
        value: (Rectangle) -> Double
    ): DoubleBinding {
        return object : DoubleBinding() {
            init {
                bind(binding)
            }

            override fun computeValue(): Double =
                value(binding.get())
        }
    }

    private fun createBoundingBox(
        pane: Pane,
        node: Node,
        properties: Array<Property<*>>
    ): ObjectBinding<Rectangle> {
        return object : ObjectBinding<Rectangle>() {
            init {
                properties.forEach { super.bind(it) }
            }

            override fun computeValue(): Rectangle {
                val bounds = node.localToScene(node.boundsInLocal)
                val paneBounds = pane.localToScene(pane.boundsInLocal)
                val sceneX = bounds.minX - paneBounds.minX
                val sceneY = bounds.minY - paneBounds.minY
                return Rectangle(sceneX, sceneY, bounds.width, bounds.height)
            }

            override fun getDependencies(): ObservableList<*> {
                return FXCollections.singletonObservableList(properties)
            }

            override fun dispose() {
                properties.forEach { super.unbind(it) }
            }
        }
    }
}
