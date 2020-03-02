package itasserui.app.viewer.ui.components

import itasserui.app.Styles
import itasserui.app.Styles.Companion.countBar
import itasserui.app.viewer.footer.FooterComponent
import itasserui.app.viewer.renderer.components.graph.GraphView
import itasserui.lib.pdb.parser.PDB
import javafx.beans.binding.Bindings
import javafx.beans.binding.IntegerBinding
import javafx.beans.binding.StringBinding
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import tornadofx.*

class CountDataController : Controller() {
    val graph: GraphView by inject()
    val bondsCountProperty = "".toProperty()
    val bondsCountString by bondsCountProperty

    val atomsCountProperty = "".toProperty()
    val atomsCountString by atomsCountProperty
    val nodes = FXCollections.observableArrayList<Any>()
    val bonds = FXCollections.observableArrayList<Any>()
    private var atomsBinding = StatViewerBinding("# Atoms: ", Bindings.size(nodes))
    private var bondBinding = StatViewerBinding("# Bonds: ", Bindings.size(bonds))
    private val gc get() = graph.controller

    init {
        atomsCountProperty.bind(atomsBinding)
        bondsCountProperty.bind(bondBinding)
        gc.pdbProperty.onChange {
            if (it is PDB) {
                println(nodes)
                nodes.clear()
                nodes.addAll(*gc.nodeViews.toTypedArray())
                println(nodes)
                bonds.clear()
                bonds.addAll(*gc.edgeViews.toTypedArray())
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


class CountView : FooterComponent() {
    val graph: GraphView by inject()
    val controller: CountDataController by inject()
    val model: CountDataModel by inject()
    override val root = hbox {
        maxHeight = 25.0
        minHeight = 25.0
        paddingAll = 5.0
        addClass(countBar)
        model.item = controller
        label(model.bondsCountString)
        separator(Orientation.VERTICAL) { paddingAll = 5.0 }
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