@file:Suppress("unused")

package itasserui.app.viewer.pdbview3d

import itasserui.app.viewer.pdbmodel.Atom
import itasserui.app.viewer.pdbmodel.Bond
import itasserui.app.viewer.pdbmodel.Residue
import itasserui.app.viewer.pdbmodel.SecondaryStructure
import itasserui.app.viewer.view.Presenter
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.Group
import javafx.scene.Node
import java.util.*

/**
 * Graph view representation in 2 dimensional space.
 *
 * @author Patrick Grupp
 */
class MyGraphView3D
/**
 * Constructor for the graph representation in the view. The model needs to make sure, that all nodes represented
 * by its edges are already persisted in the model. otherwise this will produce errors.
 *
 * @param presenter The view presenter
 */
    (
    /**
     * The presenter to be called for queries.
     */
    private val presenter: Presenter
) : Group() {
    /**
     * List of view's node representation. Can ONLY contain objects of type [MyNodeView3D].
     */
    private val nodeViewGroup: Group

    /**
     * List of residues as a view ribbon representation. Can ONLY contain objects of type [MyRibbonView3D].
     */
    private val residueViewGroup: Group

    /**
     * List of view's edge representation. Can ONLY contain objects of type [MyEdgeView3D].
     */
    private val edgeViewGroup: Group

    /**
     * List of view's secondary structure representation. Can ONLY contain objects of type [MySecondaryStructureView3D].
     */
    private val secondaryStructureViewGroup: Group

    /**
     * Maps model to view of nodes.
     */
    private val modelToNode: MutableMap<Atom, MyNodeView3D>

    /**
     * Maps model to view of edges.
     */
    private val modelToEdge: MutableMap<Bond, MyEdgeView3D>

    /**
     * Maps model residues to view residues.
     */
    private val modelToResidue: MutableMap<Residue, MyRibbonView3D>

    /**
     * Maps model SecondaryStructures to view's secondary structures [MySecondaryStructureView3D].
     */
    private val modelToStructure: MutableMap<SecondaryStructure, MySecondaryStructureView3D>

    /**
     * Property determining the radius of the bonds.
     */
    private val bondRadiusScaling: DoubleProperty

    /**
     * Property determining the scaling factor for the radius of the atoms.
     */
    private val atomRadiusScaling: DoubleProperty

    /**
     * Get all node views.
     *
     * @return All view.View instances representing a node.
     */
    val nodeViews: List<Node>
        get() {
            val ret = ArrayList<Node>()
            ret.addAll(nodeViewGroup.children)
            return ret
        }

    /**
     * Get all edge views.
     *
     * @return All view instance representing an edge.
     */
    val edgeViews: List<Node>
        get() {
            val ret = ArrayList<Node>()
            ret.addAll(edgeViewGroup.children)
            return ret
        }

    init {
        modelToNode = HashMap()
        modelToEdge = HashMap()
        modelToResidue = HashMap()
        modelToStructure = HashMap()
        nodeViewGroup = Group()
        edgeViewGroup = Group()
        residueViewGroup = Group()
        secondaryStructureViewGroup = Group()
        this.bondRadiusScaling = SimpleDoubleProperty(1.0)
        this.atomRadiusScaling = SimpleDoubleProperty(1.0)

        this.children.add(edgeViewGroup)
        this.children.add(nodeViewGroup)
        this.children.add(residueViewGroup)
        this.children.add(secondaryStructureViewGroup)

        // Make invisible on startup
        residueViewGroup.isVisible = false
        secondaryStructureViewGroup.isVisible = false
    }

    /**
     * Add a note to the view.
     *
     * @param atom The model node to be added.
     */
    fun addNode(atom: Atom) {
        // Create new view node
        val node = MyNodeView3D(atom, this.atomRadiusScaling)
        // Set up the view logic in the presenter for this node.
        presenter.setUpNodeView(node)
        // Add the node to the scene graph
        nodeViewGroup.children.add(node)
        // Add to mapping for later use
        modelToNode[atom] = node
    }

    /**
     * Remove a node from the view. NOTE: Assumes the edges have already been deleted by the model.
     *
     * @param atom The model node to be removed.
     */
    fun removeNode(atom: Atom) {
        // Filter for view's node to be removed through all view nodes.
        if (modelToNode.containsKey(atom)) {

            val current = modelToNode[atom]
            nodeViewGroup.children.remove(current)
            modelToNode.remove(atom)
        } else
            System.err.println("Error in node removal, list size is not equal to 1.")

    }

    /**
     * Adds a new edge to the view. NOTE: Both nodes it conects need to exist already in the view model.
     *
     * @param bond The model's edge to be represented.
     */
    fun addEdge(bond: Bond) {
        val sourceNode = bond.source
        val targetNode = bond.target

        //Find the view representation of source and target
        val source = modelToNode[sourceNode]
        val target = modelToNode[targetNode]


        // source and target nodes found? then add the edge. else print an error
        if (source != null && target != null) {
            // Create new view edge
            val tmp = MyEdgeView3D(bond, source, target, this.bondRadiusScaling)
            // Add edge to the scene graph
            edgeViewGroup.children.add(tmp)
            modelToEdge[bond] = tmp
        } else {
            System.err.println("Source or target node not found, could not create view edge.")
        }
    }

    /**
     * Remove an edge from the view.
     *
     * @param bond The model's edge to be removed.
     */
    fun removeEdge(bond: Bond) {
        // Filter all view edges for the one to be removed
        val toBeRemoved = modelToEdge[bond]
        // Remove the found one -> should only be one
        edgeViewGroup.children.remove(toBeRemoved)
        modelToEdge.remove(bond)
    }

    /**
     * Add a residue in order to build up the ribbon view of the graph view.
     * @param residue Residue to be added to the continuous ribbon.
     */
    fun addResidue(residue: Residue) {
        val ribbon = MyRibbonView3D(residue)
        residueViewGroup.children.add(ribbon)
        modelToResidue[residue] = ribbon
    }

    /**
     * Remove a residue from the residues list. This will remove the residue from the continuous ribbon.
     * @param residue Residue to be removed.
     */
    fun removeResidue(residue: Residue) {
        val ribbonToRemove = modelToResidue[residue]
        residueViewGroup.children.remove(ribbonToRemove)
        modelToResidue.remove(residue)
    }

    /**
     * Add a secondary structure to the graph view. SecondaryStructures are used in order to show the cartoon view
     * of the graph. The secondary structures do not need to be consecutive.
     * @param structure The structure to be represented in the view.
     */
    fun addSecondaryStructure(structure: SecondaryStructure) {
        val cartoon = MySecondaryStructureView3D(structure)
        secondaryStructureViewGroup.children.add(cartoon)
        modelToStructure[structure] = cartoon
    }

    /**
     * Remove a secondary structure from the graph view. SecondaryStructures are used in order to show the cartoon view
     * of the graph. The secondary structures do not need to be consecutive.
     * @param structure The structure to be removed from the view.
     */
    fun removeSecondaryStructure(structure: SecondaryStructure) {
        val cartoonToBeRemoved = modelToStructure[structure]
        secondaryStructureViewGroup.children.remove(cartoonToBeRemoved)
        modelToStructure.remove(structure)
    }

    /**
     * Get the view node by model node.
     *
     * @param atom The model instance.
     * @return The corresponding view node instance.
     */
    fun getNodeByModel(atom: Atom): MyNodeView3D {
        return modelToNode[atom]!!
    }


    /**
     * Get the view edge by model edge.
     *
     * @param bond The model instance for which the view edge should be returned.
     * @return The view instance corresponding to the given model.
     */
    fun getEdgeByModel(bond: Bond): MyEdgeView3D {
        return modelToEdge[bond]!!
    }

    /**
     * Hides the edges.
     *
     * @param hide Specifies if to hide, or to show the edges.
     */
    fun hideEdges(hide: Boolean) {
        edgeViewGroup.isVisible = !hide
    }

    /**
     * Hides the nodes.
     *
     * @param hide Specifies if to hide, or to show the edges.
     */
    fun hideNodes(hide: Boolean) {
        nodeViewGroup.isVisible = !hide
    }

    /**
     * Hide given node.
     *
     * @param node Node to be visible or hidden.
     * @param hide Hide the node if true, else show the node.
     */
    fun hideNode(node: MyNodeView3D, hide: Boolean) {
        node.isVisible = !hide
    }

    /**
     * Hide given edge.
     *
     * @param edge Edge to be visible or hidden.
     * @param hide Hide the given edge if true, else show the edge.
     */
    fun hideEdge(edge: MyEdgeView3D, hide: Boolean) {
        edge.isVisible = !hide
    }

    /**
     * Property to scale the radius of a bond.
     * @return Property to scale the radius of a bond.
     */
    fun bondRadiusScalingProperty(): DoubleProperty {
        return bondRadiusScaling
    }

    /**
     * Property to scale the radius of an atom.
     * @return Property to scale the radius of an atom.
     */
    fun atomRadiusScalingProperty(): DoubleProperty {
        return atomRadiusScaling
    }

    /**
     * Show/Hide the ribbon view.
     * @param hide Hide the ribbon view if true, else show it.
     */
    fun ribbonView(hide: Boolean) {
        this.residueViewGroup.isVisible = !hide
    }

    /**
     * Hides and shows the cartoon view of the loaded PDB.
     * @param hide True if cartoon should be hidden, False if cartoon should be shown.
     */
    fun cartoonView(hide: Boolean) {
        this.secondaryStructureViewGroup.children.stream().map { el -> el as MySecondaryStructureView3D }
            .forEach { structure ->
                if (!structure.wasComputed()) {
                    structure.compute()
                }
            }
        this.secondaryStructureViewGroup.isVisible = !hide
    }


}
