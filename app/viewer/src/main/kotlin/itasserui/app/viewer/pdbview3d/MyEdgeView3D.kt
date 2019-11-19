package itasserui.app.viewer.pdbview3d

import itasserui.app.viewer.pdbmodel.Bond
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Group
import javafx.scene.paint.Color

/**
 * view.View of an edge in 2 dimensional space. NOTE: Always add the two nodes to the model, before adding the connecting edge.
 *
 * @author Patrick Grupp
 */
class MyEdgeView3D
/**
 * Constructor
 *
 * @param reference     Reference to the model edge.
 * @param source        The Source node
 * @param target        The Target node
 * @param radiusScaling The scaling factor with which the radius will be scaled.
 */
internal constructor(
    /**
     * Get the reference to the model's edge.
     *
     * @return Model edge represented by this view representation.
     */
    internal val modelEdgeReference: Bond,
    /**
     * Get the view's source node.
     *
     * @return view.View source node.
     */
    val sourceNodeView: MyNodeView3D,
    /**
     * Get the view's target node.
     *
     * @return view.View target ndoe.
     */
    val targetNodeView: MyNodeView3D, radiusScaling: DoubleProperty
) : Group() {

    /**
     * Get the edge's line shape.
     *
     * @return The line of the edge.
     */
    val line: MyLine3D
    private val radius: DoubleProperty
    private val color: ObjectProperty<Color>

    init {
        // color for this edge
        this.color = SimpleObjectProperty(Color.LIGHTGRAY)
        this.radius = SimpleDoubleProperty()
        radius.bind(radiusScaling.multiply(3))

        // Bind line start point to source node's start coordinates
        // Bind line to end/target nodes coordinates
        line = MyLine3D(
            sourceNodeView.translateXProperty(),
            sourceNodeView.translateYProperty(),
            sourceNodeView.translateZProperty(),
            targetNodeView.translateXProperty(),
            targetNodeView.translateYProperty(),
            targetNodeView.translateZProperty(),
            radius,
            color
        )

        // Add line to scene graph/ this group
        this.children.add(line)
    }

    /**
     * Get the radius property. Determining the lines radius.
     *
     * @return Radius property.
     */
    fun radiusProperty(): DoubleProperty {
        return this.radius
    }


    /**
     * Get the color property. Determines the line's color. Default is lightgray.
     *
     * @return Color property of the edge.
     */
    fun colorProperty(): ObjectProperty<Color> {
        return this.color
    }
}
