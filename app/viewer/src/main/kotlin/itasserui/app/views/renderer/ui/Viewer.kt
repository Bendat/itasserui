package itasserui.app.views.renderer.ui

import itasserui.app.views.renderer.components.graph.GraphView
import itasserui.app.views.renderer.ui.components.countview
import itasserui.app.views.test.TestFragment
import itasserui.lib.pdb.parser.PDBParser
import javafx.geometry.Orientation
import javafx.geometry.Point3D
import javafx.scene.PerspectiveCamera
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.control.TabPane
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import tornadofx.*
import java.nio.file.Paths
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
                    togglegroup {
                        radiobutton("Atom View", this) {
                            isSelected = true
                        }
                        radiobutton("Cartoon View", this) {}
                    }
                    separator(Orientation.VERTICAL)
                    button("Run Blast")
                    separator(Orientation.VERTICAL)

                    checkbox("Ribbon View")
                    checkbox("Show bonds")
                    checkbox("Show C-Betas")
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
                        radiobutton("By Element", this).isSelected = true
                        radiobutton("By Residue", this)
                        radiobutton("By Secondary Structure", this)
                    }

                    button("Reset Position") {
                        setOnMouseClicked {
                            graph.controller.reset()
                        }
                    }
                }
            }
        }
        center {
            tabpane {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                tab("PDB Viewer") {
                    val file = Paths.get(TestFragment::class.java.getResource("/1ey4.pdb").file)
                    val pdb = PDBParser.parse(file)
                    stackpane {
                        prefHeight = 400.0
                        pdb.map { pdb ->
                            graph.pdb = pdb
                            pane {
                                val subscene = SubScene(graph.root, 500.0, 500.0, true, SceneAntialiasing.BALANCED)
                                children.add(subscene)
                                subscene.widthProperty().bind(widthProperty())
                                subscene.heightProperty().bind(heightProperty())
                                val perspectiveCamera = PerspectiveCamera(true)
                                perspectiveCamera.nearClip = 0.1
                                perspectiveCamera.farClip = GraphView.PaneDepth * 2
                                perspectiveCamera.translateZ = -GraphView.PaneHeight / 2
                                subscene.camera = perspectiveCamera
                            }
                        }
                    }

                    setOnMousePressed { event ->
                        controller.pressedX = event.sceneX
                        controller.pressedY = event.sceneY
                        event.consume()
                    }

                    setOnMouseDragged { event ->
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

                        event.consume()
                    }

                    setOnScroll { event ->
                        val delta = 0.01 * event.deltaY + 1
                        val focus = graph.controller.computePivot()
                        val scale = Scale(delta, delta, delta, focus.x, focus.y, focus.z)
                        val value = scale.createConcatenation(graph.controller.worldTransform)
                        graph.controller.worldTransformProperty.value = value

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
                countview(graph.controller.nodeViews, graph.controller.edgeViews)
            }
        }


    }


}

