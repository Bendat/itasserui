package itasserui.app.views.renderer.edge

import itasserui.app.viewer.pdbmodel.Bond
import itasserui.app.views.renderer.atom.node.NodeRenderer
import itasserui.app.views.renderer.edge.line.Line
import itasserui.app.views.renderer.edge.line.LineView
import itasserui.app.views.renderer.edge.line.Pos
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import tornadofx.getValue
import tornadofx.setValue

class EdgeController(
    val bond: Bond,
    val source: NodeRenderer,
    val target: NodeRenderer
) {
    val color = SimpleObjectProperty(Color.LIGHTGRAY)
    var line = makeLineView()
    val radiusProperty = SimpleDoubleProperty(1.0)
    var radius by radiusProperty

    private fun makeLineView(): LineView {
        val startPos = Pos(
            source.root.translateXProperty(),
            source.root.translateYProperty(), source.root.translateZProperty()
        )
        val endPos = Pos(
            target.root.translateXProperty(),
            target.root.translateYProperty(), target.root.translateZProperty()
        )
        val line = Line(startPos, endPos)
        return LineView(line, radiusProperty, color)
    }
}