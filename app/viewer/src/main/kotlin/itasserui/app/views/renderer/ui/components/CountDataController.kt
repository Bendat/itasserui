package itasserui.app.views.renderer.ui.components

import itasserui.app.views.renderer.components.graph.GraphView
import javafx.beans.binding.Bindings
import javafx.beans.binding.IntegerBinding
import javafx.beans.binding.StringBinding
import javafx.collections.FXCollections
import javafx.event.EventTarget
import javafx.geometry.Orientation
import javafx.scene.layout.HBox
import tornadofx.*

class CountDataController(val graph: GraphView) : Controller() {
    val bondsCountProperty = "".toProperty()
    val bondsCountString by bondsCountProperty

    val atomsCountProperty = "".toProperty()
    val atomsCountString by atomsCountProperty
    val nodes = FXCollections.observableArrayList<Any>()
    val bonds = FXCollections.observableArrayList<Any>()
    private var atomsBinding = StatViewerBinding("# Atoms: ", Bindings.size(nodes))
    private var bondBinding = StatViewerBinding("# Bonds: ", Bindings.size(bonds))

    init {
        atomsCountProperty.bind(atomsBinding)
        bondsCountProperty.bind(bondBinding)
        graph.controllerProperty.onChange {
            if (it != null) {
                nodes.clear()
                nodes.addAll(*it.nodeViews.toTypedArray())
                bonds.clear()
                bonds.addAll(*it.edgeViews.toTypedArray())
            }
        }

    }
}

class CountDataModel : ItemViewModel<CountDataController>() {
    val bondsCount = bind(CountDataController::bondsCountProperty)
    val atomsCount = bind(CountDataController::atomsCountProperty)
    val bondsCountString = bind(CountDataController::bondsCountProperty)
    val atomsCountString = bind(CountDataController::atomsCountProperty)
}

fun EventTarget.countview(
    graph: GraphView,
    op: HBox.(CountView) -> Unit = {}
): HBox {
    val view = CountView(graph)
    return opcr(this, view.root, { op(view) })
}

class CountView(
    val graph: GraphView
) : View() {
    val controller = CountDataController(graph)
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