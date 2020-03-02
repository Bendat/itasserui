package itasserui.app.viewer.renderer.components.graph

import itasserui.app.viewer.events.PDBLoadedEvent
import itasserui.app.viewer.renderer.components.ribbon.RibbonFragment
import itasserui.app.viewer.renderer.data.atom.AtomFragment
import itasserui.app.viewer.renderer.data.edge.EdgeFragment
import itasserui.app.viewer.renderer.data.structure.SecondaryStructureController
import itasserui.app.viewer.renderer.data.structure.SecondaryStructureView
import itasserui.common.extensions.compose
import itasserui.common.extensions.ifTrue
import itasserui.lib.pdb.parser.*
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.geometry.Point3D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.transform.Rotate
import javafx.scene.transform.Transform
import tornadofx.*

object EmptyGroup : Group()

@Suppress("unused", "MemberVisibilityCanBePrivate")
class GraphController : Controller() {
    val scaling: ScalingController by inject()
    val pdbProperty = SimpleObjectProperty<ParsedPDB>(UninitializedPDB)
    var pdb by pdbProperty

    val worldTransformProperty = SimpleObjectProperty<Transform>(Rotate())
    var worldTransform by worldTransformProperty

    val view: GraphView by inject()

    val nodeViewGroupProperty = defaultGroup
    var nodeViewGroup: Group by nodeViewGroupProperty

    val residueViewGroupProperty = defaultGroup
    var residueViewGroup: Group by residueViewGroupProperty

    val edgeGroupProperty = defaultGroup
    var edgeGroup: Group by edgeGroupProperty

    val secondaryStructureGroupProperty = defaultGroup
    var secondaryStructureGroup: Group by secondaryStructureGroupProperty

    val modelToNode: ObservableMap<Atomic, AtomFragment> = observableHashMap<Atomic, AtomFragment>()
    val modelToEdge: ObservableMap<Bond, EdgeFragment> = observableHashMap<Bond, EdgeFragment>()
    val modelToResidue: ObservableMap<Residue, RibbonFragment> = observableHashMap<Residue, RibbonFragment>()
    val modelToStructure: ObservableMap<SecondaryStructure, SecondaryStructureView> =
        observableHashMap<SecondaryStructure, SecondaryStructureView>()

    val bondRadiusScalingProperty = scaling.bonds
    var bondRadiusScaling by bondRadiusScalingProperty

    val atomRadiusScalingProperty = scaling.atoms
    var atomRadiusScaling by atomRadiusScalingProperty

    val nodeViews: ObservableList<Node> get() = nodeViewGroup.children
    val edgeViews: ObservableList<Node> get() = edgeGroup.children
    val cAlphaBetas: ObservableList<Node> = FXCollections.observableArrayList()
    val cBetas: ObservableList<Node> = FXCollections.observableArrayList()
    val residueAtoms = observableHashMap<Residue, List<AtomFragment>>()

    private val defaultGroup get() = Group().toProperty()
    internal val defaultTransform = worldTransform

    init {
        setWorldTransformListener()
        subscribe<PDBLoadedEvent> {
            pdb = it.pdb
        }
        pdbProperty.onChange { parsed ->
            when (parsed) {
                is PDB -> {
                    clearNodeLists()
                    createAtomFragments(parsed)
                    mapParsedToLists(parsed)
                    residueViewGroup.isVisible = false
                }
            }
        }
    }

    private fun GraphController.mapParsedToLists(parsed: PDB) {
        pdb.edges.forEach { add(it) }
        pdb.structures.forEach { add(it) }

        parsed.residues.forEach { add(it) }
    }

    private fun clearNodeLists() {
        edgeViews.clear()
        nodeViews.clear()
        modelToEdge.clear()
        modelToNode.clear()
        modelToResidue.clear()
        modelToStructure.clear()
    }

    private fun createAtomFragments(pdb: PDB) = pdb.residues.map { residue ->
        residueAtoms[residue] = residue.atoms.mapNotNull {
            if (it is NormalizedAtom) add(it) else null
        }
    }

    private fun setWorldTransformListener() =
        worldTransformProperty.addListener { _, _, n -> view.root.transforms.setAll(n) }


    fun add(atom: NormalizedAtom): AtomFragment {
        val view = AtomFragment(atom, atomRadiusScalingProperty, "")
        nodeViewGroup.children += view.root
        modelToNode[atom] = view
        atom.isCBeta.ifTrue { cBetas += view.root }
        return view
    }

    fun add(bond: Bond) {
        val source = modelToNode[bond.from]
        val target = modelToNode[bond.to]
        (source compose target){ source, target ->
            val view = EdgeFragment(source, target, bondRadiusScalingProperty)
            edgeGroup.children += view.root
            bond.isCAlphaBeta.ifTrue { cAlphaBetas += view.root }
            modelToEdge[bond] = view
        }
    }

    fun add(residue: Residue) {
        val ribbon = RibbonFragment(residue)
        residueViewGroup.children += ribbon.root
        modelToResidue[residue] = ribbon
    }

    fun add(secondaryStructure: SecondaryStructure) {
        val view = SecondaryStructureView(secondaryStructure)
        secondaryStructureGroup.children += view.root
        modelToStructure[secondaryStructure] = view
    }

    fun showCartoonView(shouldShow: Boolean) {
        secondaryStructureGroup.children
            .map { it.userData }
            .filterIsInstance<SecondaryStructureController>()
            .forEach { it.compute() }
        secondaryStructureGroup.isVisible = shouldShow
    }

    internal fun computePivot(): Point3D {
        // Use the local bound for computation of the midpoint
        val b = view.root.boundsInLocal
        val x = b.maxX - b.width / 2
        val y = b.maxY - b.height / 2
        val z = b.maxZ - b.depth / 2
        return Point3D(x, y, z)
    }
}

@Suppress("unused")
class GraphModel : ItemViewModel<GraphController>() {
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

class GraphView : View() {
    val scaling: ScalingController by inject()
    val controller: GraphController by inject()

    init {
        setControllerListener()
    }

    override val root = group {}

    fun bind(atomScaling: DoubleProperty, bondScaling: DoubleProperty) {
        scaling.atoms.bind(atomScaling)
        scaling.bonds.bind(bondScaling)
    }

    private fun setControllerListener() {
        controller.pdbProperty.onChange { change ->
            println("Change is $change")
            val pdb = controller
            root.children.clear()
            if (change is PDB)
                root.children.addAll(pdb.edgeGroup, pdb.nodeViewGroup,
                    pdb.residueViewGroup, pdb.secondaryStructureGroup)
        }
    }


}



