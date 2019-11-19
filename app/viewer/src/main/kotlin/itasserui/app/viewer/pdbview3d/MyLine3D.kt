package itasserui.app.viewer.pdbview3d

import javafx.beans.InvalidationListener
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.geometry.Point3D
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Cylinder

/**
 * JavaFX class representing a line in three dimensional space, using a cylinder
 */
class MyLine3D : Group {
    private var cy: Cylinder? = null

    constructor(
        startXProperty: DoubleProperty, startYProperty: DoubleProperty, startZProperty: DoubleProperty,
        endXProperty: DoubleProperty, endYProperty: DoubleProperty, endZProperty: DoubleProperty,
        radiusProperty: DoubleProperty, color: ObjectProperty<Color>
    ) {
        // Initialize the shape
        cy = Cylinder()

        // Bind the radius to the EdgeView's radius property
        cy!!.radiusProperty().bind(radiusProperty)
        // Set the shape's color and highlighting color
        val mat = PhongMaterial()
        mat.diffuseColorProperty().bind(color)
        color.addListener { event -> mat.specularColor = color.value.brighter() }
        cy!!.material = mat

        // Add shape to scene graph
        this.children.add(cy)

        val listener = InvalidationListener {
            // create points of the start and end coordinates
            val startPoint = Point3D(startXProperty.value!!, startYProperty.value!!, startZProperty.value!!)
            val endPoint = Point3D(endXProperty.value!!, endYProperty.value!!, endZProperty.value!!)
            // center where to set the cylinders center (midpoint between start and end)
            val centerOfCylinder = startPoint.midpoint(endPoint)
            // y axis point
            val yAxis = Point3D(0.0, 1.0, 0.0)

            // Compute a point representing the direction the shape should represent
            val directionPoint = endPoint.subtract(startPoint)

            // Compute the rotation axis
            val rotationAxis = directionPoint.crossProduct(yAxis)

            // angle
            val angle = -directionPoint.angle(yAxis)

            // Compute the height of the cylinder
            val heightOfCylinder = endPoint.distance(startPoint)


            // Use the computed values in order to set the cylinders properties appropriately
            cy!!.rotationAxis = rotationAxis
            cy!!.rotate = angle
            cy!!.translateX = centerOfCylinder.x
            cy!!.translateY = centerOfCylinder.y
            cy!!.translateZ = centerOfCylinder.z
            cy!!.height = heightOfCylinder
        }

        // Add the listener to all properties in order to react to changes
        startXProperty.addListener(listener)
        startYProperty.addListener(listener)
        startZProperty.addListener(listener)

        endXProperty.addListener(listener)
        endYProperty.addListener(listener)
        endZProperty.addListener(listener)

        // invalidate initially
        listener.invalidated(startXProperty)
    }

    constructor(
        startX: Double,
        startY: Double,
        startZ: Double,
        endX: Double,
        endY: Double,
        endZ: Double,
        radius: Double,
        color: Color
    ) {
        // Initialize the shape
        cy = Cylinder()

        // Bind the radius to the EdgeView's radius property
        cy!!.radiusProperty().value = radius
        // Set the shape's color and highlighting color
        val mat = PhongMaterial()
        mat.diffuseColorProperty().value = color
        mat.specularColor = color.brighter()
        cy!!.material = mat

        // Add shape to scene graph
        this.children.add(cy)


        // create points of the start and end coordinates
        val startPoint = Point3D(startX, startY, startZ)
        val endPoint = Point3D(endX, endY, endZ)
        // center where to set the cylinders center (midpoint between start and end)
        val centerOfCylinder = startPoint.midpoint(endPoint)
        // y axis point
        val yAxis = Point3D(0.0, 1.0, 0.0)

        // Compute a point representing the direction the shape should represent
        val directionPoint = endPoint.subtract(startPoint)

        // Compute the rotation axis
        val rotationAxis = directionPoint.crossProduct(yAxis)

        // angle
        val angle = -directionPoint.angle(yAxis)

        // Compute the height of the cylinder
        val heightOfCylinder = endPoint.distance(startPoint)


        // Use the computed values in order to set the cylinders properties appropriately
        cy!!.rotationAxis = rotationAxis
        cy!!.rotate = angle
        cy!!.translateX = centerOfCylinder.x
        cy!!.translateY = centerOfCylinder.y
        cy!!.translateZ = centerOfCylinder.z
        cy!!.height = heightOfCylinder

    }
}
