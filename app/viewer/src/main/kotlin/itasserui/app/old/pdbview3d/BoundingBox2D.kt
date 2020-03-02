package itasserui.app.old.pdbview3d

import javafx.beans.binding.DoubleBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.Property
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.SubScene
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 *
 */
class BoundingBox2D
/**
 * Generate a group with all 2D rectangles enclosing a node.
 *
 * @param pane              The pane containing the 3D objects.
 * @param node              NodeView3D to be bounded by this box.
 * @param transformProperty The transformation property of the world.
 * @param subScene          The SubScene on which the 3D graph is set on.
 */
    (pane: Pane, node: MyNodeView3D, transformProperty: Property<*>, subScene: SubScene) : Group() {

    init {
        val properties = arrayOf(
            transformProperty,
            node.translateXProperty(),
            node.translateYProperty(),
            node.translateZProperty(),
            node.scaleXProperty(),
            node.scaleYProperty(),
            node.scaleZProperty(),
            subScene.widthProperty(),
            subScene.heightProperty(),
            node.shape.radiusProperty()
        )
        val binding = createBoundingBoxBinding(pane, node, properties)
        val box = Rectangle()
        // Add the rectangle to this group (scene graph)
        this.children.add(box)
        this.isPickOnBounds = false
        box.stroke = Color.CORNFLOWERBLUE
        box.fill = Color(0.39215687, 0.58431375, 0.92941177, 0.3)

        box.xProperty().bind(object : DoubleBinding() {
            init {
                bind(binding)
            }

            override fun computeValue(): Double {
                return binding.get().x
            }
        })

        box.yProperty().bind(object : DoubleBinding() {
            init {
                bind(binding)
            }

            override fun computeValue(): Double {
                return binding.get().y
            }
        })

        box.scaleXProperty().bind(object : DoubleBinding() {
            init {
                bind(binding)
            }

            override fun computeValue(): Double {
                return binding.get().scaleX
            }
        })

        box.scaleYProperty().bind(object : DoubleBinding() {
            init {
                bind(binding)
            }

            override fun computeValue(): Double {
                return binding.get().scaleY
            }
        })

        box.scaleZProperty().bind(object : DoubleBinding() {
            init {
                bind(binding)
            }

            override fun computeValue(): Double {
                return binding.get().scaleZ
            }
        })

        box.heightProperty().bind(object : DoubleBinding() {
            init {
                bind(binding)
            }

            override fun computeValue(): Double {
                return binding.get().height
            }
        })

        box.widthProperty().bind(object : DoubleBinding() {
            init {
                bind(binding)
            }

            override fun computeValue(): Double {
                return binding.get().width
            }
        })
    }

    private fun createBoundingBoxBinding(
        pane: Pane,
        node: Node,
        properties: Array<Property<*>>
    ): ObjectBinding<Rectangle> {
        return object : ObjectBinding<Rectangle>() {
            init {
                for (i in properties.indices) {
                    super.bind(properties[i])
                }
            }

            override fun computeValue(): Rectangle {
                val boundsOnScreen = node.localToScreen(node.boundsInLocal)
                val paneBoundsOnScreen = pane.localToScreen(pane.boundsInLocal)
                val xInScene = boundsOnScreen.minX - paneBoundsOnScreen.minX
                val yInScene = boundsOnScreen.minY - paneBoundsOnScreen.minY
                return Rectangle(
                    xInScene.toInt().toDouble(), yInScene.toInt().toDouble(), boundsOnScreen.width.toInt().toDouble(),
                    boundsOnScreen.height.toInt().toDouble()
                )
            }

            override fun getDependencies(): ObservableList<*> {
                return FXCollections.singletonObservableList(properties)
            }

            override fun dispose() {
                for (i in properties.indices) {
                    super.unbind(properties[i])
                }
            }
        }
    }
}
