package itasserui.app.views.renderer.data.atom

import itasserui.app.viewer.pdbmodel.Bond
import itasserui.lib.pdb.parser.Atom
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Sphere
import tornadofx.*

class AtomController(atom: Atom, radiusScaling: DoubleProperty, text: String) : Controller() {
    val outEdges: ObservableList<Bond> = FXCollections.observableArrayList()
    val inEdges: ObservableList<Bond> = FXCollections.observableArrayList()

    val shapeProperty = SimpleObjectProperty(Sphere())
    val shape by shapeProperty

    val materialProperty = SimpleObjectProperty(PhongMaterial())
    val material by materialProperty

    val atomProperty = atom.toProperty()
    var atom by atomProperty

    val textProperty = text.toProperty()
    var text by textProperty

    val xCoordinateProperty = atom.position.x.toProperty()
    var xCoordinate by xCoordinateProperty
    val yCoordinateProperty = atom.position.y.toProperty()
    var yCoordinate by yCoordinateProperty
    val zCoordinateProperty = atom.position.z.toProperty()
    var zCoordinate by zCoordinateProperty

    val colorProperty = atom.element.color.toProperty()
    var color by colorProperty

    val radiusProperty = atom.element.radius.toProperty()
    var radius by radiusProperty

    val radiusScalingProperty = radiusScaling
    val radiusScaling by radiusScalingProperty

    init {
        material.diffuseColorProperty().bindBidirectional(colorProperty)
        shape.radiusProperty().bind(radiusProperty.multiply(radiusScaling))
        shape.material = material
        @Suppress("RedundantLambdaArrow")
        colorProperty
            .addListener { _ -> material.specularColor = colorProperty.value.brighter() }
    }
}



