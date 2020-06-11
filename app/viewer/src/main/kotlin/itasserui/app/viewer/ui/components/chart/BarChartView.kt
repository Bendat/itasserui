package itasserui.app.viewer.ui.components.chart

import itasserui.app.viewer.events.PDBLoadedEvent
import itasserui.app.viewer.renderer.components.graph.GraphController
import itasserui.app.viewer.renderer.components.graph.GraphView
import itasserui.common.extensions.ifNotNull
import itasserui.common.extensions.isNull
import itasserui.lib.pdb.parser.AminoAcid
import itasserui.lib.pdb.parser.PDB
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.StackedBarChart
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import tornadofx.*
import java.util.*

class BarChartController : Controller() {
    val graph: GraphView by inject()
    val controller: GraphController by inject()

    val chartProperty = SimpleObjectProperty<StackedBarChart<String, Number>>()
    var chart by chartProperty

    init {
        subscribe<PDBLoadedEvent> { create(it.pdb) }
    }

    fun create(pdb: PDB) {
        val betasheet = pdb.sheetContent
        val alphahelix = pdb.helixContent
        println("Sheet ${pdb.residues.map { it.acid }}")
        for (acid in AminoAcid.values()) {
            if ((acid !in betasheet) and (acid !in alphahelix))
                continue
            val current = Series<String, Number>()
            current.name = acid.molecule
            var data: Data<String, Number>

            if (acid in betasheet) {
                data = Data(BarChartView.beta, betasheet.getValue(acid))
                data.nodeProperty().onChange { it.isNull }
                current.data.add(data)
                data.nodeProperty()
                    .onChange { it.ifNotNull { n -> n.tooltip("${acid.molecule}, # Occurences: ${betasheet.getValue(acid)}") } }
            }

            if (acid in alphahelix) {
                data = Data(BarChartView.alpha, alphahelix.getValue(acid))
                current.data.add(data)
                data.nodeProperty()
                    .onChange {
                        it.ifNotNull { n ->
                            n.tooltip("${acid.molecule}, # Occurences: ${alphahelix.getValue(acid)}")
                        }
                    }
            }
            chart.data.add(current)
        }

    }
}

class BarChartView : View() {
    val controller: BarChartController by inject()
    val graphController: GraphController by inject()

    override val root = group {

        val xAxis = CategoryAxis().apply {
            label = "Secondary Structure"
            categories = FXCollections.observableArrayList(Arrays.asList(alpha, beta, coil))
        }
        val yAxis = NumberAxis()
        yAxis.label = "# Amino Acids"
        stackedbarchart("Amino Acids in Secondary Structures", xAxis, yAxis) {
            controller.chart = this
            fitToParentSize()
        }
    }


    companion object {
        internal val alpha = "Alpha helix"
        internal val beta = "Beta sheet"
        internal val coil = "Coil"
    }
}