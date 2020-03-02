package itasserui.app.viewer.renderer.data.edge

import itasserui.app.viewer.renderer.components.line.Line
import itasserui.app.viewer.renderer.components.line.LineView
import itasserui.app.viewer.renderer.components.line.Pos
import itasserui.app.viewer.renderer.data.atom.AtomFragment
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import tornadofx.Controller
import tornadofx.Scope
import tornadofx.getValue
import tornadofx.setValue

class EdgeController(
    val from: AtomFragment,
    val to: AtomFragment,
    scaling: DoubleProperty,
    override val scope: Scope
) : Controller() {
    val view: EdgeFragment by inject()
    val colorProperty = SimpleObjectProperty(Color.LIGHTGRAY)
    var color by colorProperty
    val radiusProperty = scaling
    var radius by radiusProperty

    internal fun makeLineView(): LineView {
        val startPos = Pos(
            from.root.translateXProperty(),
            from.root.translateYProperty(),
            from.root.translateZProperty()
        )
        val endPos = Pos(
            to.root.translateXProperty(),
            to.root.translateYProperty(),
            to.root.translateZProperty()
        )
        val line = Line(startPos, endPos)
        return LineView(line, radiusProperty, colorProperty)
    }
}
