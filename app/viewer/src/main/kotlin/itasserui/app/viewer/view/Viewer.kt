package itasserui.app.viewer.view

import javafx.geometry.Orientation
import javafx.scene.control.TabPane
import tornadofx.*

class Viewer : View("PDB Viewer") {
    override val root = borderpane {
        top {
            menubar {
                menu("File") {
                    item("Load from file...")
                    item("Open 1EY4 PDB file")
                    item("Open 2KL8 PDB file")
                    item("Open 2TGA PDB file")
                }

                menu("Edit") {
                    item("Clear PDB view")
                    item("Run BLAST")
                    item("Cancel BLAST")
                    item("Reset Rotation")
                }

                menu("View") {
                    togglegroup {
                        radiomenuitem("Show atom view", this)
                        radiomenuitem("Show cartoon view", this)
                    }

                    togglegroup {
                        radiomenuitem("Coloring by chemical element", this)
                        radiomenuitem("Coloring by residue", this)
                        radiomenuitem("Coloring by secondary structure", this)
                    }
                    item("Show atoms")
                    item("Show bonds")
                    item("Show C-Betas")
                }
            }
        }
        center {
            vbox {
                toolbar {
                    togglegroup {
                        radiobutton("Atom View", this) { }
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
                        slider(0.3, 3.0, 1.0) {
                            isShowTickMarks = true
                            isShowTickLabels = true
                        }
                    }

                    hbox {
                        label("Scale Edges")
                        slider(0.3, 3.0, 1.0) {
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
                }
            }
        }
        bottom {
            borderpane {
                top {
                    scrollpane {
                        flowpane { }
                    }
                }

                center {
                    tabpane {
                        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                        tab("PDB Viewer") {
                            borderpane {
                                top {
                                    buttonbar {
                                        button("Run BLAST")
                                        button("Cancel BLAST").prefWidth = 50.0
                                    }
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
                    hbox {
                        hbox {
                            hbox {
                                label("# Bonds: ")
                                label("684")
                            }
                            separator(Orientation.VERTICAL) {
                                paddingAll = 2.0
                            }
                            hbox {
                                label("# Atoms: ")
                                label("854")
                            }

                        }
                    }
                }
            }
        }
    }
}

class ViewerController{

}