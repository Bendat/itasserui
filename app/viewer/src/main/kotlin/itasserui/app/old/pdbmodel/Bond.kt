package itasserui.app.old.pdbmodel

import javafx.beans.property.*
import tornadofx.*
/**
 * Edge representation.
 *
 * @author Patrick Grupp
 */
class Bond(from: Atom, to: Atom, text: String) {
    /**
     * The source node of the edge
     */
    val sourceProperty: ObjectProperty<Atom> = SimpleObjectProperty(from)
    var source by sourceProperty
    /**
     * The target node of the edge
     */
    val targetProperty: ObjectProperty<Atom> = SimpleObjectProperty(to)
    var target by targetProperty
    /**
     * The edge's text
     */
    val textProperty: StringProperty = SimpleStringProperty(text)
    var text by textProperty
    /**
     * The edge's weight
     */
    val weightProperty: DoubleProperty = SimpleDoubleProperty()
    var weight by weightProperty
}
