@file:Suppress("MemberVisibilityCanBePrivate")

package itasserui.app.views.renderer.atom

import itasserui.app.viewer.pdbmodel.Atom
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Sphere
import tornadofx.Controller
import tornadofx.ItemViewModel
import tornadofx.getValue

@Suppress("RedundantLambdaArrow")
class NodeController(atom: Atom, radiusScaling: DoubleProperty) : Controller() {
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
        atom.colorProperty
            .addListener { _ -> material.specularColor = atom.colorProperty.value.brighter() }

    }
}
