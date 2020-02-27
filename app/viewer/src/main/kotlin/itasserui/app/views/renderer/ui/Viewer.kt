package itasserui.app.views.renderer.ui

import itasserui.app.events.PDBLoadedEvent
import itasserui.app.views.renderer.components.canvas.CanvasView
import itasserui.app.views.renderer.components.graph.GraphView
import itasserui.app.views.renderer.ui.components.countview
import itasserui.common.extensions.ifTrue
import itasserui.lib.pdb.parser.PDBParser
import javafx.geometry.Orientation
import javafx.geometry.Point3D
import javafx.scene.control.TabPane
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import javafx.stage.FileChooser
import tornadofx.*
import kotlin.math.pow
import kotlin.math.sqrt

class Viewer : View("PDB Viewer") {
    val graph by inject<GraphView>()
    val controller: ViewerController by inject()
    override val root = borderpane {
        graph.bind(controller.nodesScale, controller.edgeScale)
        top {
            vbox {
                toolbar {
                    button("Open") {
                        setOnAction {
                            val filter = FileChooser.ExtensionFilter(
                                "PDB files (.pdb, .PDB)",
                                "*.pdb", "*.PDB"
                            )
                            val files = chooseFile("Select a PDB file", arrayOf(filter))
                            if (files.any()) {
                                val file = files.first().toPath()
                                val parsePDB = PDBParser.parse(file)
                                print(parsePDB)
                                parsePDB.map {
                                    fire(PDBLoadedEvent(it))
                                }
                            }
                        }
                    }
                    togglegroup {
                        radiobutton("Atom View", this) {
                            isSelected = true
                            selectedProperty().onChange {
                                graph.controller.nodeViewGroup.isVisible = it
                                graph.controller.edgeGroup.isVisible = it
                            }
                        }
                        radiobutton("Cartoon View", this) {
                            selectedProperty().onChange { graph.controller.showCartoonView(it) }
                        }
                    }
                    separator(Orientation.VERTICAL)
                    button("Run Blast")
                    separator(Orientation.VERTICAL)

                    checkbox("Ribbon View") {
                        selectedProperty().onChange { graph.controller.residueViewGroup.isVisible = it }
                    }
                    checkbox("Show Atoms") {
                        isSelected = true
                        selectedProperty().onChange { graph.controller.nodeViewGroup.isVisible = it }
                    }
                    checkbox("Show bonds") {
                        isSelected = true
                        selectedProperty().onChange { graph.controller.edgeGroup.isVisible = it }


                    }
                    checkbox("Show C-Betas") {
                        isSelected = true
                        selectedProperty().onChange {
                            graph.controller.cAlphaBetas.forEach { node -> node.isVisible = it }
                            graph.controller.cBetas.forEach { node -> node.isVisible = it }
                        }

                    }
                }
                toolbar {
                    hbox {
                        label("Scale Nodes")
                        slider(0.0, 3.0, 1.0) {
                            graph.atomScaling.bind(valueProperty())
                            isShowTickMarks = true
                            isShowTickLabels = true
                        }
                    }

                    hbox {
                        label("Scale Edges")
                        slider(0.3, 3.0, 1.0) {
                            graph.bondScaling.bind(valueProperty())
                            minorTickCount = 2
                            isShowTickMarks = true
                            isShowTickLabels = true
                        }
                    }

                    togglegroup {
                        radiobutton("By Element", this) {
                            isSelected = true
                            selectedProperty().onChange { controller.colorByAtom() }
                        }

                        radiobutton("By Residue", this) {
                            selectedProperty().onChange {
                                it.ifTrue { controller.colorByResidue() }
                            }
                        }
                        radiobutton("By Secondary Structure", this) {
                            selectedProperty().onChange {
                                it.ifTrue { controller.colorBySecondaryStructure() }
                            }
                        }

                        button("Reset Position") {
                            setOnMouseClicked {
                                controller.reset()
                            }
                        }
                    }
                }
            }
            center {
                tabpane {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                    tab("PDB Viewer") {
                        add<CanvasView>()
                        setOnMousePressed { event ->
                            controller.pressedX = event.sceneX
                            controller.pressedY = event.sceneY
                            event.consume()
                        }

                        setOnMouseDragged { event ->
                            if (graph.controller != null) {
                                val deltaX = event.sceneX - controller.pressedX
                                val deltaY = event.sceneY - controller.pressedY

                                // Get the perpendicular axis for the dragged point
                                val direction = Point3D(deltaX, deltaY, 0.0)
                                val axis = direction.crossProduct(0.0, 0.0, 1.0)
                                val angle = 0.4 * sqrt(deltaX.pow(2.0) + deltaY.pow(2.0))

                                //compute the main focus of the world and use it as pivot
                                val focus = graph.controller.computePivot()

                                val rotation = Rotate(angle, focus.x, focus.y, focus.z, axis)

                                // Apply the rotation as an additional transform, keeping earlier modifications
                                graph.controller.worldTransform =
                                    rotation.createConcatenation(graph.controller.worldTransform)

                                // Set the variables new
                                controller.pressedX = event.sceneX
                                controller.pressedY = event.sceneY
                            }
                            event.consume()
                        }

                        setOnScroll { event ->
                            if (graph.controller != null) {
                                val delta = 0.01 * event.deltaY + 1
                                val focus = graph.controller.computePivot()
                                val scale = Scale(delta, delta, delta, focus.x, focus.y, focus.z)
                                val value = scale.createConcatenation(graph.controller.worldTransform)
                                graph.controller.worldTransformProperty.value = value
                            }

                        }
                    }
                    tab("Stats") {
                        borderpane {
                            top {
                                buttonbar {
                                    button("Run BLAST")
                                    button("Cancel BLAST").prefWidth = 50.0
                                }
                            }
                        }
                    }
                    tab("Blast") {
                        borderpane {
                            top {
                                buttonbar {
                                    button("Run BLAST")
                                    button("Cancel BLAST").prefWidth = 50.0
                                }
                            }
                        }
                    }
                }
            }

            bottom {
                pane {
                    countview(graph)
                }
            }


        }

    }
}

