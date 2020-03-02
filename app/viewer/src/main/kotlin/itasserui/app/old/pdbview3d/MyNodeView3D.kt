package itasserui.app.old.pdbview3d

import itasserui.app.old.pdbmodel.Atom
import javafx.beans.property.DoubleProperty
import javafx.scene.Group
import javafx.scene.control.Tooltip
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Sphere

/**
 * Node representation in 2 dimensional view.
 *
 * @author Patrick Grupp
 */
// Intellij gets confused by lambdas which implement
// interfaces
@Suppress("RedundantLambdaArrow")
class MyNodeView3D
/**
 * Constructs a View representation of a node.
 *
 * @param node          The model's node this view object represents.
 * @param radiusScaling The scaling factor, with which the radius will be scaled.
 */
internal constructor(
    /**
     * Get the referenced model node this view object represents.
     *
     * @return Model's node instance, this view node represents.
     */
    val modelNodeReference: Atom,
    radiusScaling: DoubleProperty
) : Group() {
    val shape: Sphere
    var material: PhongMaterial


    init {
        // Install a tooltip with the nodes text
        val tooltip = Tooltip()
        tooltip.textProperty().bind(modelNodeReference.textProperty)
        Tooltip.install(this, tooltip)
        // Draw the circular sphere which represents a node
        shape = Sphere()
        shape.radiusProperty().bind(modelNodeReference.radiusProperty.multiply(radiusScaling))
        // Get color from model

        material = PhongMaterial()
        material.diffuseColorProperty().bindBidirectional(modelNodeReference.colorProperty)
        // The listener listens on the models color property and adapts the specular color of the material accordingly

        modelNodeReference.colorProperty
            .addListener { _ -> material.specularColor = modelNodeReference.colorProperty.value.brighter() }

        shape.material = material

        // Add the sphere to the scene graph
        this.children.add(shape)

        // Set the position of the node in the two dimensional space. Placing is handled by the view.Presenter, therefore not
        // computed here
        this.translateXProperty().bind(modelNodeReference.xCoordinateProperty)
        this.translateYProperty().bind(modelNodeReference.yCoordinateProperty)
        this.translateZProperty().bind(modelNodeReference.zCoordinateProperty)
    }// Set reference to model instance, in order to identify the node

    /**
     * Set another color for the node.
     *
     * @param col The color to be set.
     */
    internal fun setColor(col: Color) {
        material.diffuseColorProperty().setValue(col)
    }
}
