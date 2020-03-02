package itasserui.app.viewer.ui

import itasserui.app.viewer.renderer.components.graph.GraphView
import itasserui.app.viewer.renderer.data.atom.AtomFragment
import itasserui.lib.pdb.parser.Alphahelix
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.paint.Color
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue
import tornadofx.toProperty
import java.util.*

class ViewerController : Controller() {
    val graph: GraphView by inject()

    val edgeScale = SimpleDoubleProperty(1.0)
    val nodesScale = SimpleDoubleProperty(1.0)
    val pressedXProperty = 0.0.toProperty()
    var pressedX by pressedXProperty
    val pressedYProperty = 0.0.toProperty()
    var pressedY by pressedYProperty
    private val controller get() = graph.controller
    fun colorByAtom() {
        controller.nodeViews.map { it.userData }
            .map { it as AtomFragment }
            .map { it.controller }
            .forEach { it.color = it.atom.element.color }
        controller.modelToEdge.values.forEach { it.controller.color = Color.GRAY }
    }

    fun colorByResidue() {
        val randomGenerator = Random()
        controller.residueAtoms.map { entry ->
            val r = randomGenerator.nextFloat()
            val g = randomGenerator.nextFloat()
            val b = randomGenerator.nextFloat()
            val col = Color(r.toDouble(), g.toDouble(), b.toDouble(), 1.0)
            entry.value.forEach { it.controller.color = col }
            controller.modelToEdge.values.forEach {
                if (it.controller.from in entry.value)
                    it.controller.color = col
            }
        }
    }

    fun colorBySecondaryStructure() {
        val random = Random()
        controller.modelToStructure.keys.forEach { ss ->
            controller.residueAtoms.map { res ->
                val randColor = Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), 1.0)
                val color = when {
                    controller.modelToStructure
                        .any { res.key in it.key } -> randColor
                    ss.structureType == Alphahelix -> Color.RED
                    else -> Color.CORNFLOWERBLUE
                }
                res.value.forEach { it.controller.color = color }
                controller.modelToEdge.values.forEach {
                    if (it.controller.from in res.value)
                        it.controller.color = color
                }
            }
        }
    }

    fun reset() {
        controller.worldTransform = controller.defaultTransform
    }
}