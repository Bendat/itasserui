package itasserui.app.viewer.ui.components.tab.viewer.toolbars

import itasserui.app.viewer.renderer.components.graph.GraphView
import itasserui.app.viewer.renderer.components.graph.ScalingController
import itasserui.app.viewer.ui.ViewerController
import javafx.geometry.Orientation
import tornadofx.*

class PDBScalingToolbarView : View() {
    private val controller: ViewerController by inject()
    private val scaling: ScalingController by inject()
    private val graph: GraphView by inject()
    override val root = toolbar {
        button("Reset") {
            setOnMouseClicked {
                controller.reset()
            }
        }
        hbox {
            label("Scale Nodes")
            slider(0.0, 3.0, 1.0) {
                scaling.atoms.bind(valueProperty())
                isShowTickMarks = true
                isShowTickLabels = true
                majorTickUnit = 1.0
                minorTickCount = 3
                isSnapToTicks = true
            }
        }

        hbox {
            label("Scale Edges")
            slider(0.0, 3.0, 1.0) {
                scaling.bonds.bind(valueProperty())
                isShowTickMarks = true
                isShowTickLabels = true
                majorTickUnit = 1.0
                minorTickCount = 3
            }
        }
        separator(Orientation.VERTICAL)
        checkbox("Show C-Betas") {
            isSelected = true
            selectedProperty().onChange {
                graph.controller.cAlphaBetas.forEach { node -> node.isVisible = it }
                graph.controller.cBetas.forEach { node -> node.isVisible = it }
            }

        }
    }

}