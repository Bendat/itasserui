@file:Suppress("MemberVisibilityCanBePrivate")

package itasserui.app.old.pdbmodel

import itasserui.app.old.pdbmodel.SecondaryStructure.StructureType
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.getValue
import tornadofx.setValue
import tornadofx.sizeProperty
import java.util.*
import kotlin.collections.set
import kotlin.streams.toList

/**
 * An entry of the Protein Data Bank (PDB) containing all Residues, which in turn contain all atoms associated with
 * them, and all secondary structures.
 *
 * @author Patrick Grupp
 */
class PDBEntry {

    /**
     * Observable list of all nodes
     */
    val nodes: ObservableList<Atom> = FXCollections.observableArrayList()
    /**
     * Observable list of all edges
     */
    val edges: ObservableList<Bond> = FXCollections.observableArrayList()

    /**
     * The pdb entry's secondary structures as an observable list.
     */
    val secondaryStructures: ObservableList<SecondaryStructure> = FXCollections.observableArrayList()

    /**
     * The pdb entry's residues as an observable list.
     */
    val residues: ObservableList<Residue> = FXCollections.observableArrayList()

    /**
     * Title of the PDB file shortly describing the protein shown.
     */
    val titleProperty: StringProperty = SimpleStringProperty()
    var title by titleProperty
    /**
     * The four letter code of the shown pdb structure.
     */
    val pdbCodeProperty: StringProperty = SimpleStringProperty()
    var pdbCode by pdbCodeProperty
    /**
     * Get the number of edges in the graph
     *
     * @return number of edges in the graph
     */
    val numberOfEdges: Int
            by edges.sizeProperty

    /**
     * get the number of nodes in the graph
     *
     * @return number of nodes in the graph
     */
    val numberOfNodes: Int
            by nodes.sizeProperty

    /**
     * Get the number of secondary structures.
     *
     * @return the number of secondary structures.
     */
    val numberOfSecondaryStructures: Int
            by secondaryStructures.sizeProperty

    val numberOfResidues get() = residues.size
    /**
     * Get the whole protein's sequence for BLASTing.
     *
     * @return Sequence of the currently loaded protein.
     */
    val sequence: String
        get() {
            val resultingSequence = StringBuilder()
            for (r in residues) {
                resultingSequence.append(r.oneLetterAminoAcidName)
            }
            return resultingSequence.toString()
        }

    /**
     * Get all bonds connecting the C alpha and C beta residue of all residues in this PDB entry.
     *
     * @return All Ca -> Cb bonds.
     */
    // The source and target relation is always like that, since it is always set that way when parsing a PDB file.
    val allCAlphaCBetaBonds: ArrayList<Bond>
        get() {
            val bonds = edges.filter { e ->
                e.source.chemicalElementProperty.value == Atom.ChemicalElement.CA
                        && e.target.chemicalElementProperty.value == Atom.ChemicalElement.CB
            }
            return ArrayList(bonds)
        }

    /**
     * Get all C - O bonds in this PDB entry.
     *
     * @return List of all Bonds connecting the C and O atom in all residues.
     */
    val allCOBonds: ArrayList<Bond>
        get() {
            val bonds = edges.filter { e ->
                e.source.chemicalElementProperty.value == Atom.ChemicalElement.C
                        && e.target.chemicalElementProperty.value == Atom.ChemicalElement.O
            }.toList()
            return ArrayList(bonds)
        }

    /**
     * Get all O atoms in the PDB entry.
     *
     * @return List of all O atoms in this entry.
     */
    val allOAtoms: ArrayList<Atom>
        get() {
            val atoms =
                this.nodes
                    .filter { e -> e.chemicalElementProperty.value == Atom.ChemicalElement.O }
                    .toList()
            return ArrayList(atoms)
        }

    /**
     * Gets all C beta atoms in the PDB entry
     *
     * @return List of all C beta atoms in this entry.
     */
    val allCBetaAtoms: ArrayList<Atom>
        get() {
            val atoms = this.nodes.stream()
                .filter { atom -> atom.chemicalElementProperty.value == Atom.ChemicalElement.CB }
                .toList()
            return ArrayList(atoms)
        }

    /**
     * Get the content of each amino acid in alpha helices.
     * @return Content in alpha helices.
     */
    val alphaHelixContent: HashMap<Residue.AminoAcid, Int>
        get() = getHelix(StructureType.alphahelix)

    /**
     * Get the content of each amino acid in beta sheets.
     * @return Content in beta sheets.
     */
    val betaSheetContent: HashMap<Residue.AminoAcid, Int>
        get() = getHelix(StructureType.betasheet)


    /**
     * Get the content of each Amino acid in coils.
     * @return Content in coil
     */
    val coilContent: HashMap<Residue.AminoAcid, Int>
        get() = getHelix(null)


    private fun getHelix(structureType: StructureType?): HashMap<Residue.AminoAcid, Int> {
        val result = HashMap<Residue.AminoAcid, Int>()
        for (r in residues) {
            if (r.secondaryStructure != null && r.secondaryStructure?.secondaryStructureType == structureType) {
                if (result.containsKey(r.aminoAcid)) {
                    result[r.aminoAcid] = result[r.aminoAcid]!! + 1
                } else {
                    result[r.aminoAcid] = 1
                }
            }
        }
        return result
    }

    /**
     * Get a [ObservableList] of all [Atom]s in the Graph.
     *
     * @return List of nodes in the graph.
     */
    fun nodesProperty(): ObservableList<Atom> {
        return this.nodes
    }

    /**
     * Get a [ObservableList] of all [Bond]s in the Graph.
     *
     * @return List of edges in the graph.
     */
    fun edgesProperty(): ObservableList<Bond> {
        return edges
    }

    /**
     * Get a [ObservableList] of all [SecondaryStructure]s in the PDB Entry.
     *
     * @return All secondary structures noted in the given PDB pdbFile.
     */
    fun secondaryStructuresProperty(): ObservableList<SecondaryStructure> {
        return secondaryStructures
    }

    /**
     * Get the short description of the entry (protein).
     *
     * @return Property holding the Title of the PDB entry, a short protein description.
     */
    fun titleProperty(): StringProperty {
        return this.titleProperty
    }

    /**
     * Get the pdb ID property.
     *
     * @return Property holding the PDB ID.
     */
    fun pdbCodeProperty(): StringProperty {
        return this.pdbCodeProperty
    }

    /**
     * Add residue to the list of residues of the model.
     *
     * @param res
     */
    fun addResidue(res: Residue) {
        residues.add(res)
    }

    /**
     * Add a node to the graph.
     *
     * @param n Node to be added to the graph.
     */
    fun addNode(n: Atom) {
        this.nodes.add(n)
    }

    /**
     * Get node in nodes list with index idx.
     *
     * @param idx Index of node in list.
     * @return Node requested.
     */
    fun getNode(idx: Int): Atom {
        return this.nodes[idx]
    }

    /**
     * Remove a node from the graph an remove edges connecting it.
     *
     * @param n Node to be deleted.
     */
    fun removeNode(n: Atom) {
        val edgesToBeRemoved =
            edges.stream().filter { p -> p.target == n || p.source == n }
        for (e in edgesToBeRemoved) {
            deleteEdge(e)
        }
        this.nodes.remove(n)
    }

    /**
     * Connect the given nodes with a new edge.
     *
     * @param source The source node.
     * @param target The target node
     */
    @Throws(GraphException::class)
    fun connectNodes(source: Atom, target: Atom) {
        val connection = Bond(source, target, "")
        connectNodes(connection)
    }

    /**
     * Connect nodes n1 and n2 with edge e.
     *
     * @param e edge for connecting the two nodes.
     * @throws GraphException if edge already exists
     */
    @Throws(GraphException::class)
    fun connectNodes(e: Bond) {
        if (!graphContainsEdge(e)) {
            // Add new nodes if necessary
            if (!this.nodes.contains(e.source))
                addNode(e.source)
            if (!this.nodes.contains(e.target))
                addNode(e.target)

            edges.add(e)
            e.source.addOutEdge(e)
            e.target.addInEdge(e)
        } else
            throw GraphException("Edge already exists")
    }

    /**
     * Disconnect two nodes by removing the edge connecting the source with the target.
     *
     * @param source First node.
     * @param target Second node.
     */
    internal fun disconnectNodes(source: Atom, target: Atom) {
        edges.filter { p -> p.source == source && p.target == target }
            .map { deleteEdge(it) }
    }

    /**
     * Delete edge from graph.
     *
     * @param e edge to be removed from graph
     */
    fun deleteEdge(e: Bond) {
        e.source.removeOutEdge(e)
        e.target.removeInEdge(e)
        edges.remove(e)
    }

    /**
     * Does the graph already contain this edge.
     *
     * @param e The edge to be checked, if it is contained in the graph.
     * @return true if edge e is contained in the graph, else false.
     */
    private fun graphContainsEdge(e: Bond): Boolean =
        edges.any { p -> p.source == e.source && p.target == e.target }

    /**
     * Resets the graph to initial state, deleting all nodes and edges (implicitly):
     */
    fun reset() {
        edges.clear()
        this.nodes.clear()
        secondaryStructures.clear()
        residues.clear()
        title = ""
        pdbCodeProperty().value = ""
    }

    /**
     * Get the bonds which are internal part of a residue. Without the peptide bond.
     *
     * @param residue The residue for which the bonds should be returned.
     * @return Intra residual bonds.
     */
    fun getBondsOfResidue(residue: Residue): MutableList<Bond> {
        return edges.filter { e ->
            e.source == residue.nAtom && e.target == residue.cAlphaAtom ||
                    e.source == residue.cAlphaAtom && e.target == residue.cBetaAtom ||
                    e.source == residue.cAlphaAtom && e.target == residue.cAtom ||
                    e.source == residue.cAtom && e.target == residue.oAtom
        }.toMutableList()
    }

}
