package itasserui.app.views.renderer.components.graph

import itasserui.app.views.renderer.components.ribbon.RibbonController
import itasserui.app.views.renderer.components.ribbon.RibbonFragment
import itasserui.app.views.renderer.components.ribbon.RibbonModel
import itasserui.app.views.renderer.data.atom.AtomController
import itasserui.app.views.renderer.data.atom.AtomFragment
import itasserui.app.views.renderer.data.atom.AtomViewModel
import itasserui.app.views.renderer.data.edge.EdgeController
import itasserui.app.views.renderer.data.edge.EdgeView
import itasserui.app.views.renderer.data.edge.EdgeViewModel
import itasserui.app.views.renderer.data.structure.SecondaryStructureController
import itasserui.app.views.renderer.data.structure.SecondaryStructureModel
import itasserui.app.views.renderer.data.structure.SecondaryStructureView
import itasserui.common.extensions.compose
import itasserui.common.extensions.having
import itasserui.lib.pdb.parser.Atom
import itasserui.lib.pdb.parser.Bond
import itasserui.lib.pdb.parser.Residue
import itasserui.lib.pdb.parser.SecondaryStructure
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.scene.Group
import javafx.scene.Node
import tornadofx.*

object EmptyGroup : Group()

@Suppress("unused", "MemberVisibilityCanBePrivate")
class GraphController : Controller() {
    val view: GraphView by inject()
    val children get() = view.root.children

    val nodeViewGroupProperty = defaultGroup
    var nodeViewGroup: Group by nodeViewGroupProperty

    val residueViewGroupProperty = defaultGroup
    var residueViewGroup: Group by residueViewGroupProperty

    val edgeGroupProperty = defaultGroup
    var edgeGroup: Group by edgeGroupProperty

    val secondaryStructureGroupProperty = defaultGroup
    var secondaryStructureGroup: Group by secondaryStructureGroupProperty

    val modelToNode: ObservableMap<Atom, AtomFragment> = observableHashMap<Atom, AtomFragment>()
    val modelToEdge: ObservableMap<Bond, EdgeView> = observableHashMap<Bond, EdgeView>()
    val modelToResidue: ObservableMap<Residue, RibbonFragment> = observableHashMap<Residue, RibbonFragment>()
    val modelToStructure: ObservableMap<SecondaryStructure, SecondaryStructureView> =
        observableHashMap<SecondaryStructure, SecondaryStructureView>()

    val bondRadiusScalingProperty = 1.0.toProperty()
    var bondRadiusScaling by bondRadiusScalingProperty

    val atomRadiusScalingProperty = 1.0.toProperty()
    var atomRadiusScaling by atomRadiusScalingProperty

    val nodeViews: ObservableList<Node> get() = nodeViewGroup.children
    val edgeViews: ObservableList<Node> get() = edgeGroup.children

    private val defaultGroup get() = Group().toProperty()

    fun remove(atom: Atom) {
        modelToNode.having(atom) { nodeViewGroup.children.remove(it.root) }
    }

    fun add(atom: Atom) {
        val controller = AtomController(atom, atomRadiusScalingProperty, "")
        val model = AtomViewModel(controller)
        val scope = Scope(controller, model)
        val view = AtomFragment(scope)
        TODO("     presenter.setUpNodeView(node)")
        nodeViewGroup.children += view.root
        modelToNode[atom] = view
    }

    fun add(bond: Bond) {
        val source = modelToNode[bond.from]
        val target = modelToNode[bond.to]
        (source compose target){ source, target ->
            val scope = Scope(source, target)
            val controller = EdgeController(scope)
            val model = EdgeViewModel(controller, scope)
            setInScope(model, scope)
            val view = EdgeView(scope)
            edgeGroup.children += view.root
            modelToEdge[bond] = view
        }
    }

    fun add(residue: Residue) {
        val scope = Scope()
        val controller = RibbonController(residue, scope)
        RibbonModel(controller, scope)
        val ribbon = RibbonFragment(scope)
        residueViewGroup.children += ribbon.root
        modelToResidue[residue] = ribbon
    }

    fun add(secondaryStructure: SecondaryStructure) {
        val scope = Scope()
        val cartoon = SecondaryStructureController(secondaryStructure, scope)
        SecondaryStructureModel(cartoon, scope)
        val view = SecondaryStructureView(scope)
        secondaryStructureGroup.children += view.root
        modelToStructure[secondaryStructure] = view
    }

    fun showCartoonView(shouldShow: Boolean) {
        secondaryStructureGroup.children
            .filter { it.userData is SecondaryStructureView }
            .map { it.userData as SecondaryStructureView }
            .forEach { it.controller.compute() }
        secondaryStructureGroup.isVisible = shouldShow
    }
}

@Suppress("unused")
class GraphControllerModel(controller: GraphController) :
    ItemViewModel<GraphController>(controller) {
    val nodeViewGroup = bind(GraphController::nodeViewGroupProperty)
    val residueViewGroup = bind(GraphController::residueViewGroupProperty)
    val edgeGroup = bind(GraphController::edgeGroupProperty)
    val secondaryStructureGroup = bind(GraphController::secondaryStructureGroupProperty)
    val modelToNode = bind(GraphController::modelToNode)
    val modelToEdge = bind(GraphController::modelToEdge)
    val modelToResidue = bind(GraphController::modelToResidue)
    val modelToStructure = bind(GraphController::modelToStructure)
    val bondRadiusScaling = bind(GraphController::bondRadiusScalingProperty)
    val atomRadiusScaling = bind(GraphController::atomRadiusScalingProperty)
    val nodeViews = bind(GraphController::nodeViews)
    val edgeViews = bind(GraphController::edgeViews)
}

class GraphView(override val scope: Scope) : View() {
    val model by inject<GraphControllerModel>()
    val controller by inject<GraphController>()
    override val root = group {
        children.addAll(controller.edgeGroup, controller.nodeViewGroup,
            controller.residueViewGroup, controller.secondaryStructureGroup)
        controller.residueViewGroup.isVisible = true
        controller.secondaryStructureGroup.isVisible = false
    }
}



