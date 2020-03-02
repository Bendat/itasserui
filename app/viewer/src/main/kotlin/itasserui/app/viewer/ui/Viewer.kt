package itasserui.app.viewer.ui

import itasserui.app.viewer.blast.BlastServiceView
import itasserui.app.viewer.events.PDBLoadedEvent
import itasserui.app.viewer.footer.FooterComponent
import itasserui.app.viewer.footer.FooterView
import itasserui.app.viewer.renderer.components.graph.GraphController
import itasserui.app.viewer.renderer.components.graph.GraphView
import itasserui.app.viewer.renderer.components.graph.ScalingController
import itasserui.app.viewer.ui.components.CountView
import itasserui.app.viewer.ui.components.tab.viewer.ViewTabView
import itasserui.lib.pdb.parser.PDBParser
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.stage.FileChooser
import tornadofx.*

class Viewer : View("PDB Viewer") {
    val graph by inject<GraphView>()
    val controller: ViewerController by inject()
    private val footer: FooterView by inject()
    private val countView: CountView by inject()
    private val emptyFooter: FooterComponent.EmptyFooter by inject()
    private val gc: GraphController by inject()
    private val scaling: ScalingController by inject()

    init {
        reloadViewsOnFocus()
    }

    override val root = vbox {
        prefWidth = 500.0
        tabpane {
            minWidthProperty().bind(this@vbox.widthProperty())
            minHeightProperty().bind(this@vbox.heightProperty())
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            prefHeight = 300.0
            tab("Open") {
                setOnSelectOpenTab(this, this@tabpane)
            }
            tab("View") {
                select()
                add<ViewTabView>()
            }
            tab("Stats") {
                borderpane {
                    top {
                        buttonbar {
                            button("Cancel BLAST").prefWidth = 50.0
                        }
                    }
                }
            }
            tab("Blast") {
                openPDBButton()
                add<BlastServiceView>()
            }
        }
    }

    private fun setOnSelectOpenTab(tab: Tab, tabPane: TabPane) {
        tab.selectedProperty().onChange {
            if (it) {
                tabPane.tabs[1].select()
                val filter = FileChooser.ExtensionFilter(
                    "PDB files (.pdb, .PDB)",
                    "*.pdb", "*.PDB"
                )
                val files = chooseFile("Select a PDB file", arrayOf(filter))
                if (files.any()) {
                    val file = files.first().toPath()
                    val parsePDB = PDBParser.parse(file)
                    parsePDB.map { parsed ->
                        fire(PDBLoadedEvent(parsed))
                    }
                }
            }
        }
    }

    private fun openPDBButton() = button("Open") {
        setOnAction {
            val filter = FileChooser.ExtensionFilter(
                "PDB files (.pdb, .PDB)",
                "*.pdb", "*.PDB"
            )
            val files = chooseFile("Select a PDB file", arrayOf(filter))
            if (files.any()) {
                val file = files.first().toPath()
                val parsePDB = PDBParser.parse(file)
                parsePDB.map {
                    fire(PDBLoadedEvent(it))
                }
            }
        }
    }

}

