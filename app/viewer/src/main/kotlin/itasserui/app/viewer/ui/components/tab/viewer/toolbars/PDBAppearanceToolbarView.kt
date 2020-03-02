package itasserui.app.viewer.ui.components.tab.viewer.toolbars

import itasserui.app.viewer.renderer.components.graph.GraphView
import itasserui.app.viewer.ui.ViewerController
import itasserui.common.extensions.ifTrue
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

class PDBAppearanceToolbarView : View() {
    val graph by inject<GraphView>()
    private val controller: ViewerController by inject()
    override val root = toolbar {
        togglegroup {
            radiobutton("Atom View", this) {
                isSelected = true
                selectedProperty().onChange {
                    graph.controller.nodeViewGroup.isVisible = it
                    graph.controller.edgeGroup.isVisible = it
                }
            }
            radiobutton("Cartoon View", this) {
                selectedProperty().onChange { graph.controller.showCartoonView(it) }
            }
        }
        separator()
        togglegroup {
            enableWhen {
                val prop = SimpleBooleanProperty(false)
                prop.bind(Bindings.isNotNull(graph.controller.pdbProperty))
                prop
            }
            label("Coloring: ")
            radiobutton("By Element", this) {
                isSelected = true
                selectedProperty().onChange { controller.colorByAtom() }
            }

            radiobutton("By Residue", this) {
                selectedProperty().onChange {
                    it.ifTrue { controller.colorByResidue() }
                }
            }
            radiobutton("By Secondary Structure", this) {
                selectedProperty().onChange {
                    it.ifTrue { controller.colorBySecondaryStructure() }
                }
            }

        }

    }


}