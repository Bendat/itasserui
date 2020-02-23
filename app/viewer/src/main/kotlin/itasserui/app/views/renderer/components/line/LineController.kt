package itasserui.app.views.renderer.components.line

import javafx.beans.InvalidationListener
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point3D
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Cylinder
import tornadofx.*

data class Pos(
    val x: DoubleProperty,
    val y: DoubleProperty,
    val z: DoubleProperty
)

data class Line(
    val start: Pos,
    val end: Pos
) {
    constructor(start: Point3D, end: Point3D) :
            this(Pos(start.x.toProperty(), start.y.toProperty(), start.z.toProperty()),
                Pos(end.x.toProperty(), end.y.toProperty(), end.z.toProperty()))
}

class LineController(
    val line: Line,
    val radiusProperty: DoubleProperty,
    val color: ObjectProperty<Color>
) : Controller() {
    val cylinderProperty = SimpleObjectProperty(Cylinder())
    val cylinder by cylinderProperty

    init {
        cylinder.radiusProperty().bind(radiusProperty)
        val material = PhongMaterial()
        material.diffuseColorProperty().bind(color)
        color.addListener { _ -> material.specularColor = color.value.brighter() }
        cylinder.material = material
        val listener = InvalidationListener {
            val startPoint = Point3D(line.start.x.value, line.start.y.value, line.start.z.value)
            val endPoint = Point3D(line.end.x.value, line.end.y.value, line.end.z.value)
            val centerOfCylinder = startPoint.midpoint(endPoint)
            val yAxis = Point3D(0.0, 1.0, 0.0)
            val directionPoint = endPoint.subtract(startPoint)
            val rotationAxis = directionPoint.crossProduct(yAxis)
            val angle = -directionPoint.angle(yAxis)
            val heightOfCylinder = endPoint.distance(startPoint)
            cylinder.rotationAxis = rotationAxis
            cylinder.rotate = angle
            cylinder.translateX = centerOfCylinder.x
            cylinder.translateY = centerOfCylinder.y
            cylinder.translateZ = centerOfCylinder.z
            cylinder.height = heightOfCylinder
        }
        line.start.x.addListener(listener)
        line.start.y.addListener(listener)
        line.start.z.addListener(listener)

        line.end.x.addListener(listener)
        line.end.y.addListener(listener)
        line.end.z.addListener(listener)
        listener.invalidated(line.start.x)
    }
}

class LineModel : ItemViewModel<LineController>() {
    val line = bind(LineController::line)
    val radius = bind(LineController::radiusProperty)
    val color = bind(LineController::color)
    val cylinder = bind(LineController::cylinderProperty)
}

class LineView(
    val line: Line,
    val radiusProperty: DoubleProperty,
    val color: ObjectProperty<Color>
) : View() {
    val controller = LineController(line, radiusProperty, color)
    override val root = group {
        children += controller.cylinder
    }
}
