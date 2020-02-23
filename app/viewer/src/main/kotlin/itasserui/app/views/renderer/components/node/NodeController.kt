@file:Suppress("MemberVisibilityCanBePrivate")

import itasserui.app.views.renderer.data.atom.AtomController
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.SubScene
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Sphere
import tornadofx.Controller
import tornadofx.getValue

class NodeController(
    atom: AtomController,
    radiusScaling: DoubleProperty,
    subscene: SubScene
) : Controller() {
    val shapeProperty = SimpleObjectProperty(Sphere())
    val shape by shapeProperty

    val materialProperty = SimpleObjectProperty(PhongMaterial())
    val material by materialProperty

    val atomProperty = SimpleObjectProperty(atom)
    val atom by atomProperty

    val radiusScalingProperty = radiusScaling
    val radiusScaling by radiusScalingProperty

    init {
        material.diffuseColorProperty().bindBidirectional(atom.colorProperty)
        shape.radiusProperty().bind(atom.radiusProperty.multiply(radiusScaling))
        shape.material = material
        @Suppress("RedundantLambdaArrow")
        atom.colorProperty
            .addListener { _ -> material.specularColor = atom.colorProperty.value.brighter() }
    }
}

