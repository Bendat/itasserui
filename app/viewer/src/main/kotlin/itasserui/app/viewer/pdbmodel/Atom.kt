@file:Suppress("unused")

package itasserui.app.viewer.pdbmodel

import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.paint.Color
import tornadofx.getValue
import tornadofx.setValue

/**
 * Node representation.
 *
 * @author Patrick Grupp
 */
val defaultAtom get() = Atom(0.0, 0.0, 0.0, "A", "helio")
class Atom(x: Double, y: Double, z: Double, chemicalElement: String, text: String) {

    /**
     * Text of the node
     */
    val textProperty: StringProperty = SimpleStringProperty(text)
    var text by textProperty

    /**
     * Outgoing edges of the node
     */
    val outEdges: ObservableList<Bond> = FXCollections.observableArrayList()
    /**
     * Ingoing edges of the node
     */
    val inEdges: ObservableList<Bond> = FXCollections.observableArrayList()



    /**
     * The x coordinate as defined by PDB.
     */
    val xCoordinateProperty = SimpleDoubleProperty(x)
    var xCoordinate by xCoordinateProperty
    /**
     * The y coordinate as defined by PDB.
     */
    val yCoordinateProperty = SimpleDoubleProperty(y)
    var yCoordinate by yCoordinateProperty
    /**
     * The z coordinate as defined by PDB.
     */
    val zCoordinateProperty = SimpleDoubleProperty(z)
    var zCoordinate by zCoordinateProperty
    /**
     * The atom's chemical element (differentiating between C alpha and C beta atoms as well, although they
     * are the same chemical element).
     */
    val chemicalElementProperty  =
        SimpleObjectProperty(ChemicalElement.valueOf(chemicalElement))
    var chemicalElement: ChemicalElement by chemicalElementProperty
    /**
     * Residue containing this Atom.
     */
    val residueProperty: ObjectProperty<Residue> = SimpleObjectProperty()
    var residue by residueProperty
    /**
     * Color of the atom.
     */
    val colorProperty: ObjectProperty<Color> = SimpleObjectProperty(this.chemicalElement.color)
    var color by colorProperty

    /**
     * Weight of the node
     */
    val radiusProperty: DoubleProperty = SimpleDoubleProperty(this.chemicalElement.radius)
    var radius by radiusProperty
    enum class ChemicalElement {
        CA, CB, N, O, C;

        /**
         * Get the chemically correct ratio between radii of the elements.
         * @return correct ratio between the radii of the elements. Can be multiplied with some final constant.
         */
        val radius: Double
            get() {
                return when (this) {
                    CA, CB, C -> 12 / 1.5
                    N -> 14 / 1.5
                    O -> 16 / 1.5
                }
            }

        /**
         * Get the correct color for each element.
         * @return Correct color for each element.
         */
        //
        val color: Color
            get() {
                return when (this) {
                    CA, CB, C -> Color.web("202020")
                    N -> Color.web("2060ff")
                    O -> Color.web("ee2010")
                }
            }
    }

    /**
     * Add an edge to this node's outgoing edges. Does not check if an edge to the edge's target is already pointing
     * from this node.
     *
     * @param outEdge The edge to be added to outgoing edges.
     */
    fun addOutEdge(outEdge: Bond) {
        outEdges.add(outEdge)
    }

    /**
     * Add an edge to this node's ingoing edges. Does not check if an edge from the edge's source is already pointing to
     * this node.
     *
     * @param inEdge The edge to be added to ingoing edges.
     */
    fun addInEdge(inEdge: Bond) {
        inEdges.add(inEdge)
    }

    /**
     * Remove an edge from the node's ingoing edges.
     *
     * @param inEdge Edge to be removed.
     */
    fun removeInEdge(inEdge: Bond) {
        inEdges.remove(inEdge)
    }

    /**
     * Remove an edge from the node's outgoing edges.
     *
     * @param outEdge Edge to be removed.
     */
    fun removeOutEdge(outEdge: Bond) {
        outEdges.remove(outEdge)
    }

    override fun toString(): String {
        return "Atom(text=$text, x=$xCoordinate, y=$yCoordinate, z=$zCoordinate)"
    }
}
