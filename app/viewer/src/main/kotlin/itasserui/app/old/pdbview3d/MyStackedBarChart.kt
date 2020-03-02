package itasserui.app.old.pdbview3d

import itasserui.app.old.pdbmodel.Residue
import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.collections.FXCollections
import javafx.scene.Group
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.StackedBarChart
import javafx.scene.chart.XYChart
import javafx.scene.control.Tooltip

import java.util.Arrays
import java.util.HashMap

/**
 * Stacked Bar chart to present some stats of the loaded PDB file.
 *
 * @author Patrick Grupp
 */
class MyStackedBarChart : Group() {
    private val xAxis = CategoryAxis()
    private val yAxis = NumberAxis()
    private var stackedBarChart = StackedBarChart(xAxis, yAxis)


    fun initialize(
        aminoAcidCountAlpha: HashMap<Residue.AminoAcid, Int>,
        aminoAcidCountBeta: HashMap<Residue.AminoAcid, Int>,
        aminoAcidCountCoil: HashMap<Residue.AminoAcid, Int>,
        widthProperty: ReadOnlyDoubleProperty, heightProperty: ReadOnlyDoubleProperty
    ) {
        stackedBarChart.title = "Amino Acids in Secondary Structures"
        xAxis.label = "Secondary Structure"
        xAxis.categories = FXCollections.observableArrayList(Arrays.asList(alpha, beta, coil))
        yAxis.label = "#Amino Acids"
        for (aaType in Residue.AminoAcid.values()) {
            if (aminoAcidCountAlpha.containsKey(aaType) ||
                aminoAcidCountBeta.containsKey(aaType) ||
                aminoAcidCountCoil.containsKey(aaType)
            ) {
                val current = XYChart.Series<String, Number>()
                current.name = Residue.getName(aaType)
                var data: XYChart.Data<String, Number>
                if (aminoAcidCountAlpha.containsKey(aaType)) {
                    data = XYChart.Data(alpha, aminoAcidCountAlpha[aaType]!!)
                    current.data.add(data)
                    Tooltip.install(
                        data.node,
                        Tooltip(Residue.getName(aaType) + ", #Occurences: " + aminoAcidCountAlpha[aaType])
                    )
                }
                if (aminoAcidCountBeta.containsKey(aaType)) {
                    data = XYChart.Data(beta, aminoAcidCountBeta[aaType]!!)
                    current.data.add(data)
                    Tooltip.install(
                        data.node,
                        Tooltip(Residue.getName(aaType) + ", #Occurences: " + aminoAcidCountBeta[aaType])
                    )

                }
                if (aminoAcidCountCoil.containsKey(aaType)) {
                    data = XYChart.Data(coil, aminoAcidCountCoil[aaType]!!)
                    current.data.add(data)
                    Tooltip.install(
                        data.node,
                        Tooltip(Residue.getName(aaType) + ", #Occurences: " + aminoAcidCountCoil[aaType])
                    )
                }
                stackedBarChart.data.add(current)
            }
        }
        stackedBarChart.minWidthProperty().bind(Bindings.subtract(widthProperty, 50))
        stackedBarChart.minHeightProperty().bind(Bindings.subtract(heightProperty, 50))
        this.children.add(stackedBarChart)
    }

    /**
     * Removes the bar plot, so it can be initialized again.
     */
    fun reset() {
        stackedBarChart = StackedBarChart(xAxis, yAxis)
        this.children.clear()
    }

    companion object {

        private val alpha = "Alpha helix"
        private val beta = "Beta sheet"
        private val coil = "Coil"
    }
}
