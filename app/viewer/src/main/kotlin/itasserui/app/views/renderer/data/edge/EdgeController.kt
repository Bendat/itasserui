package itasserui.app.views.renderer.data.edge

import arrow.data.k
import itasserui.app.views.renderer.components.line.Line
import itasserui.app.views.renderer.components.line.LineView
import itasserui.app.views.renderer.components.line.Pos
import itasserui.app.views.renderer.components.node.NodeView
import itasserui.app.views.renderer.data.atom.AtomFragment
import itasserui.app.views.renderer.data.bond.BondController
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import tornadofx.*

class EdgeController(
    override val scope: Scope
): Controller() {
    val source: AtomFragment by inject()
    val target: AtomFragment by inject()
    val color = SimpleObjectProperty(Color.LIGHTGRAY)
    var line = makeLineView()
    val radiusProperty = SimpleDoubleProperty(1.0)
    var radius by radiusProperty

    private fun makeLineView(): LineView {
        val startPos = Pos(
            source.root.translateXProperty(),
            source.root.translateYProperty(),
            source.root.translateZProperty()
        )
        val endPos = Pos(
            target.root.translateXProperty(),
            target.root.translateYProperty(),
            target.root.translateZProperty()
        )
        val line = Line(startPos, endPos)
        return LineView(line, radiusProperty, color)
    }
}
