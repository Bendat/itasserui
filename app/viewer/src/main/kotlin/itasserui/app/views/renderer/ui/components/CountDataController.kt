package itasserui.app.views.renderer.ui.components

import itasserui.app.views.renderer.components.graph.GraphView
import itasserui.app.views.renderer.data.atom.AtomFragment
import itasserui.app.views.renderer.data.edge.EdgeFragment
import javafx.beans.binding.Bindings
import javafx.beans.binding.IntegerBinding
import javafx.beans.binding.StringBinding
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.geometry.Orientation
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import tornadofx.*

class CountDataController(
    val nodes: ObservableList<*>,
    val edges: ObservableList<*>
) : Controller() {
    val bondsCountProperty = "".toProperty()
    val bondsCountString by bondsCountProperty

    val atomsCountProperty = "".toProperty()
    val atomsCountString by atomsCountProperty

    init {
        val atomsBinding = StatViewerBinding("# Atoms: ", Bindings.size(nodes))
        atomsCountProperty.bind(atomsBinding)

        val edgesBinding = StatViewerBinding("# Bonds: ", Bindings.size(edges))
        bondsCountProperty.bind(edgesBinding)
    }
}

class CountDataModel : ItemViewModel<CountDataController>() {
    val nodes = bind(CountDataController::nodes)
    val edges = bind(CountDataController::edges)
    val bondsCount = bind(CountDataController::bondsCountProperty)
    val atomsCount = bind(CountDataController::atomsCountProperty)
    val bondsCountString = bind(CountDataController::bondsCountProperty)
    val atomsCountString = bind(CountDataController::atomsCountProperty)
}

fun EventTarget.countview(
    nodes: ObservableList<*>,
    edges: ObservableList<*>,
    op: HBox.(CountView) -> Unit = {}
): HBox {
    val view = CountView(nodes, edges)
    return opcr(this, view.root, { op(view) })
}

class CountView(
    val nodes: ObservableList<*>,
    val edges: ObservableList<*>
) : View() {
    val controller = CountDataController(nodes, edges)
    val model: CountDataModel by inject()
    override val root = hbox {
        paddingAll = 5.0
        model.item = controller
        label(model.bondsCountString)
        separator(Orientation.VERTICAL) {
            paddingAll = 5.0
        }
        label(model.atomsCountString)
    }
}

class StatViewerBinding internal constructor(
    internal var bindingLabel: String,
    internal var p: IntegerBinding
) : StringBinding() {

    init {
        super.bind(p)
    }

    override fun computeValue(): String {
        // Return nice String format
        return bindingLabel + p.value!!.toString()
    }
}