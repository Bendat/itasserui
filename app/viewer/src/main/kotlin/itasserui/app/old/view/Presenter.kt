package itasserui.app.old.view

import itasserui.app.old.blast.BlastService
import itasserui.app.old.pdbmodel.*
import itasserui.app.old.pdbview3d.*
import itasserui.common.logger.Logger
import javafx.animation.FadeTransition
import javafx.animation.ParallelTransition
import javafx.animation.ScaleTransition
import javafx.beans.WeakInvalidationListener
import javafx.beans.binding.Bindings
import javafx.beans.binding.IntegerBinding
import javafx.beans.binding.StringBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ListChangeListener
import javafx.concurrent.Worker
import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.geometry.Point3D
import javafx.scene.Group
import javafx.scene.PerspectiveCamera
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.ChoiceDialog
import javafx.scene.control.TabPane
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import javafx.scene.transform.Transform
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Duration
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * view.Presenter
 */
class Presenter
/**
 * Construct view.Presenter
 *
 * @param view  The view.View of the MVP implementation.
 * @param graph The model of the MVP implementation.
 */
    (
    /**
     * View to be set in the scene.
     */
    private val view: ViewerView,
    /**
     * PDB model to be represented by the view.
     */
    private val pdbModel: PDBEntry,
    /**
     * The primary stage of the view.
     */
    private val primaryStage: Stage
): Logger {

    /**
     * The scene to be set on stage.
     */
    private val subScene3d: SubScene

    /**
     * The selection model.
     */
    private val selectionModel: MySelectionModel<Residue> = MySelectionModel<Residue>()

    /**
     * Width of the graph model pane, which contains the nodes and edges.
     */
    private val PANEWIDTH = 600.0

    /**
     * Height of the graph model pane, which contains the nodes and edges.
     */
    private val PANEHEIGHT = 600.0

    /**
     * Depth of the graph model pane, which contains the nodes and edges. This is the depth of the perspective camera's
     * near and far clip's distance.
     */
    private val PANEDEPTH = 5000.0

    /**
     * view.View representation of the graph.
     */
    private val world: MyGraphView3D

    /**
     * Property indicating if an animation is running. Does not allow to click a button meanwhile.
     */
    private val animationRunning: BooleanProperty

    /**
     * Mouse last pressed position X in scene.
     */
    private var pressedX: Double = 0.toDouble()

    /**
     * Mouse last pressed position Y in scene.
     */
    private var pressedY: Double = 0.toDouble()

    /**
     * The Blast service, handling querying the current sequence to BLAST.
     */
    private val blastService: BlastService

    /**
     * Rotation of the graph on y axis.
     */
    private val worldTransformProperty = SimpleObjectProperty<Transform>(Rotate())

    private val randomGenerator: Random


    init {
        this.blastService = BlastService()
        // initial last clicked positions for X and Y coordinate
        pressedX = 0.0
        pressedY = 0.0
        view.setPaneDimensions(PANEWIDTH, PANEHEIGHT)
        primaryStage.minWidth = 1000.0
        primaryStage.minHeight = 800.0

        randomGenerator = Random(15)

        animationRunning = SimpleBooleanProperty(false)
        // initialize the view of the Graph, which in turn initialized the views of edges and nodes
        world = MyGraphView3D(this)
        // Set depthBuffer to true, since view is 3D
        this.subScene3d = SubScene(world, PANEWIDTH, PANEHEIGHT, true, SceneAntialiasing.BALANCED)
        setUpPerspectiveCamera()

        setUpModelListeners()
        setUpTransforms()
        setMenuItemRelations()
        setFileMenuActions()
        setEditMenuActions()
        setViewMenuActions()
        initializeStatsBindings()
        setUpMouseEventListeners()
        setUpSequencePaneAndSelectionModel()
        view.set3DGraphScene(this.subScene3d)
        setUpTabPane()
        setUpBlastService()
    }// The view, model and stage to be handled by this presenter

    private fun setUpBlastService() {
        //Disable the cancel button, but not if BLAST service is running or scheduled
        val cancelBlastDisableBinding = Bindings.not(
            blastService.stateProperty().isEqualTo(Worker.State.RUNNING).or(
                blastService.stateProperty().isEqualTo(Worker.State.SCHEDULED)
            )
        )
        view.cancelBlastButton.disableProperty().bind(cancelBlastDisableBinding)
        view.cancelBlastMenuItem.disableProperty().bind(cancelBlastDisableBinding)

        // Action for Run BLAST Button in the BLAST tab
        view.runBlastButton.setOnAction { event: ActionEvent ->
            runBlast()
            view.blastText.textProperty().bind(
                Bindings.concat(
                    blastService.titleProperty(),
                    "\n", blastService.messageProperty()
                )
            )
        }

        // Action for the Run Blast menu item
        view.runBlastMenuItem.setOnAction { event ->
            runBlast()
            view.blastText.textProperty().bind(
                Bindings.concat(
                    blastService.titleProperty(),
                    "\n", blastService.messageProperty()
                )
            )
        }

        view.runBLASTToolBarButton.setOnAction { event ->
            runBlast()
            view.blastText.textProperty().bind(
                Bindings.concat(
                    blastService.titleProperty(),
                    "\n", blastService.messageProperty()
                )
            )
        }

        view.cancelBlastMenuItem.setOnAction { event -> cancelBlast(false) }

        view.cancelBlastButton.setOnAction { event -> cancelBlast(false) }

        // When BLAST was cancelled
        blastService.setOnCancelled { event ->
            view.progressBar.progressProperty().unbind()
            view.progressBar.isVisible = false
            view.status.textProperty().unbind()
            view.blastText.textProperty().unbind()
            view.status.text = "BLASTing was cancelled."
        }

        // When BLAST failed
        blastService.setOnFailed { event ->
            // remove binding
            view.status.textProperty().unbind()

            view.progressBar.progressProperty().unbind()
            view.progressBar.isVisible = false

            view.blastText.textProperty().unbind()
            //blastService.getException().printStackTrace();
            val alert = Alert(
                Alert.AlertType.ERROR,
                "BLAST service failed: " + blastService.exception.message, ButtonType.OK
            )
            alert.show()
        }

        // When BLAST service changes to Running bind the progressbar to the progress and make it visible.
        blastService.setOnRunning { event ->
            // Bind the status to the blast title
            view.status.textProperty().bind(blastService.titleProperty())
            // Bind the progress bar to the service's progress
            view.progressBar.progressProperty().bind(blastService.progressProperty())
            view.progressBar.isVisible = true
            view.blastText.textProperty().bind(blastService.messageProperty())
        }

        // When BLAST succeeded
        blastService.setOnSucceeded { event ->
            view.status.textProperty().unbind()
            view.blastText.textProperty().unbind()
            view.progressBar.progressProperty().unbind()
            view.progressBar.isVisible = false
            // Show an alert, if the BLAST tab is currently not being viewed
            if (view.contentTabPane.selectionModel.selectedItem != view.blastTab) {
                val alert = Alert(
                    Alert.AlertType.INFORMATION,
                    "BLAST service finished searching for the given sequence. View the alignments in the 'BLAST' tab",
                    ButtonType.OK
                )
                alert.show()
            }
            view.status.text = "BLAST service succeded."
            // Set the result temporarily permanent (until next BLAST is run)
            view.blastText.text = blastService.value
        }
    }

    /**
     * Allows to cancel the BLAST service, if it is running. Otherwise it shows a message that the service is not
     * running. But one should never be able to call this, when the BLAST service is not running.
     * @param silently If the BLAST service is not running, then show a warning, if this is false.
     */
    private fun cancelBlast(silently: Boolean) {
        if (blastService.isRunning) {
            blastService.cancel()
        } else if (!silently) {
            val alert = Alert(
                Alert.AlertType.INFORMATION,
                "Cannot cancel the BLAST service, since it is not running.", ButtonType.OK
            )
            alert.show()
        }
    }

    /**
     * Triger BLASTing the given seqeunce.
     */
    private fun runBlast() {
        if (pdbModel.numberOfResidues > 0) {
            val toBlastSequence = pdbModel.sequence
            blastService.sequence = toBlastSequence
            if (!blastService.isRunning) {
                // If the service is run a second time, reset its state.
                if (blastService.state == Worker.State.CANCELLED ||
                    blastService.state == Worker.State.FAILED ||
                    blastService.state == Worker.State.READY ||
                    blastService.state == Worker.State.SUCCEEDED
                ) {
                    blastService.reset()
                }
                blastService.start()
            } else {
                val choiceDialogBlastRunning = ChoiceDialog("Continue", "Restart")
                choiceDialogBlastRunning.contentText =
                    "The BLAST service is still running.\nDo you want to abort " + "it and start another query?\nThis is not recommended."
                choiceDialogBlastRunning.showAndWait()
                val choice = choiceDialogBlastRunning.selectedItem
                if (choice == "Restart") {
                    view.status.textProperty().unbind()
                    view.status.text = "Restarted BLAST service. Now running."
                    blastService.restart()
                }
            }
        } else {
            System.err.println("Cannot run BLAST, when no model is loaded. Aborting")
            view.status.textProperty().value = "BLASTing not possible, load a PDB file first."
        }
    }

    /**
     * Set up actions to change the view from or to atom/bond, ribbon and cartoon view. Default to atom/bond view.
     */
    private fun setViewMenuActions() {

        view.atomViewMenuItem.selectedProperty().addListener { event ->
            if (view.atomViewMenuItem.isSelected) {
                view.coloringByElementMenuItem.selectedProperty().value = true
            }
        }

        view.cartoonViewMenuItem.selectedProperty().addListener { event ->
            if (view.cartoonViewMenuItem.isSelected) {
                world.cartoonView(false)
                view.topPane.isVisible = false
                view.showCBetaMenuItem.selectedProperty().value = false
                view.showRibbonMenuItem.selectedProperty().value = false
                view.showAtomsMenuItem.selectedProperty().value = true
                view.showBondsMenuItem.selectedProperty().value = true
                view.coloringByElementMenuItem.selectedProperty().value = true
                view.scaleEdgesSlider.value = 1.0
                view.scaleNodesSlider.value = 3.0
                // Set the color gray and radius 1 for all nodes (smoothing for coils :D)
                for (a in pdbModel.nodesProperty()) {
                    a.colorProperty.setValue(Color.LIGHTGRAY)
                    a.radiusProperty.setValue(1)
                }
                // Hide O atoms
                for (a in pdbModel.allOAtoms) {
                    world.getNodeByModel(a).isVisible = false
                }
                // Hide C=O bonds
                for (bond in pdbModel.allCOBonds) {
                    world.getEdgeByModel(bond).isVisible = false
                }
                // For all secondary structures hide the coil structure of all residues contained by the sec struc
                for (structure in pdbModel.secondaryStructuresProperty()) {
                    for (r in structure.residuesContained) {
                        // Hide all nodes within a residue which is contained by a secondary structure
                        world.getNodeByModel(r.cAlphaAtom!!).isVisible = false
                        world.getNodeByModel(r.cBetaAtom!!).isVisible = false
                        world.getNodeByModel(r.cAtom!!).isVisible = false
                        world.getNodeByModel(r.nAtom!!).isVisible = false
                        world.getNodeByModel(r.oAtom!!).isVisible = false
                        // Hide all edges within a residue which is contained by a secondary structure
                        // When alphaHelix hide the edge to calpha. Beta sheets are shown a little differently therefore do not hide the edges there
                        r.cAlphaAtom?.inEdges?.forEach { edge -> world.getEdgeByModel(edge).isVisible = false }
                        r.cAlphaAtom?.outEdges?.forEach { edge -> world.getEdgeByModel(edge).isVisible = false }
                        r.cAtom?.outEdges?.forEach { edge -> world.getEdgeByModel(edge).isVisible = false }
                    }
                    // Betasheets are shown differently than alpha helices therefore we need to show additional bonds:
                    // for the first residue in the structure show the N-> CA bond and for the last residue
                    // show C-N and N-CA bonds.
                    if (structure.secondaryStructureType == SecondaryStructure.StructureType.betasheet) {
                        structure.firstResidue.cAlphaAtom?.inEdges?.forEach { edge ->
                            world.getEdgeByModel(edge).isVisible = true
                            world.getEdgeByModel(edge).sourceNodeView.isVisible = true
                        }
                        structure.residuesContained[structure.residuesContained.size - 1].cAtom?.inEdges
                            ?.forEach { edge ->
                                world.getEdgeByModel(edge).isVisible = true
                                world.getNodeByModel(edge.target).isVisible = true
                            }
                    }
                    // Make the last C-N bond in the structure visible since it connnects the sec structure with a coil
                    val lastPeptideBondList =
                        structure.residuesContained.get(structure.residuesContained.size - 1).cAtom
                            ?.outEdges?.filter { edge ->
                            edge.source.chemicalElementProperty.value == Atom.ChemicalElement.C
                                    && edge.target.chemicalElementProperty.value == Atom.ChemicalElement.N
                        }
                    // Else the secondary structure also ends the protein sequence, so threre is no bond to set visible,
                    // since there is no peptide bond with the N of the next residue in sequence
                    if (lastPeptideBondList?.size == 1)
                        world.getEdgeByModel(lastPeptideBondList[0]).isVisible = true

                }
            } else {
                world.cartoonView(true)
                view.topPane.isVisible = true
                view.showCBetaMenuItem.selectedProperty().value = true
                for (a in pdbModel.nodesProperty()) {
                    a.colorProperty.setValue(a.chemicalElementProperty.value.color)
                    a.radiusProperty.setValue(a.chemicalElementProperty.value.radius)
                }
                // Show O atoms
                for (a in pdbModel.allOAtoms) {
                    world.getNodeByModel(a).isVisible = true
                }
                // Show C=O bonds
                for (bond in pdbModel.allCOBonds) {
                    world.getEdgeByModel(bond).isVisible = true
                }

                for (structure in pdbModel.secondaryStructuresProperty()) {
                    for (r in structure.residuesContained) {
                        // Hide all nodes within a residue which is contained by a secondary structure
                        world.getNodeByModel(r.cAlphaAtom!!).isVisible = true
                        world.getNodeByModel(r.cBetaAtom!!).isVisible = true
                        world.getNodeByModel(r.cAtom!!).isVisible = true
                        world.getNodeByModel(r.nAtom!!).isVisible = true
                        world.getNodeByModel(r.oAtom!!).isVisible = true
                        // Hide all edges within a residue which is contained by a secondary structure
                        r.cAlphaAtom?.inEdges?.forEach { edge -> world.getEdgeByModel(edge).isVisible = true }
                        r.cAlphaAtom?.outEdges?.forEach { edge -> world.getEdgeByModel(edge).isVisible = true }
                        r.cAtom?.outEdges?.forEach { edge -> world.getEdgeByModel(edge).isVisible = true }
                    }
                }
                view.scaleNodesSlider.value = 1.0
                view.scaleEdgesSlider.value = 1.0
            }

        }

        view.showRibbonMenuItem.selectedProperty()
            .addListener { event -> world.ribbonView(!view.showRibbonMenuItem.isSelected) }

        view.showBondsMenuItem.selectedProperty()
            .addListener { event -> world.hideEdges(!view.showBondsMenuItem.isSelected) }

        view.showAtomsMenuItem.selectedProperty().addListener { event ->
            //show or hide atoms
            world.hideNodes(!view.showAtomsMenuItem.isSelected)
            // Do not shown selecion indication bounding boxes, if atoms are not shown. since that does not make much sense
            view.topPane.isVisible = view.showAtomsMenuItem.isSelected
        }

        view.showCBetaMenuItem.selectedProperty().addListener { observable, oldValue, newValue ->
            // Run through all Calpha -> Cbeta bonds and show or hide them
            pdbModel.allCAlphaCBetaBonds.forEach { bond -> world.hideEdge(world.getEdgeByModel(bond), !newValue) }
            // Run through all Cbeta atoms and show or hide them
            pdbModel.allCBetaAtoms.forEach { node -> world.hideNode(world.getNodeByModel(node), !newValue) }
        }

        // Color by chemical element and make edges gray
        view.coloringByElementRadioButton.selectedProperty().addListener { event ->
            if (view.coloringByElementRadioButton.isSelected) {
                for (a in pdbModel.nodesProperty()) {
                    a.colorProperty.setValue(a.chemicalElementProperty.getValue().color)
                }
                for (edge in world.edgeViews) {
                    (edge as MyEdgeView3D).colorProperty().setValue(Color.LIGHTGRAY)
                }
            }
        }

        // Color each residue with its own random color
        view.coloringByResidueMenuItem.selectedProperty().addListener { event ->
            if (view.coloringByResidueMenuItem.isSelected) {
                for (residue in pdbModel.residues) {
                    val r = randomGenerator.nextFloat()
                    val g = randomGenerator.nextFloat()
                    val b = randomGenerator.nextFloat()
                    val col = Color(r.toDouble(), g.toDouble(), b.toDouble(), 1.0)
                    residue.cBetaAtom?.colorProperty?.value = col
                    residue.cAlphaAtom?.colorProperty?.value = col
                    residue.nAtom?.colorProperty?.value = col
                    residue.cAtom?.colorProperty?.value = col
                    residue.oAtom?.colorProperty?.value = col
                    pdbModel.getBondsOfResidue(residue)
                        .forEach { bond -> world.getEdgeByModel(bond).colorProperty().setValue(col) }
                }
            }
        }

        // Color bonds and atoms by secondary structure
        view.coloringBySecondaryMenuItem.selectedProperty().addListener { event ->
            if (view.coloringBySecondaryMenuItem.isSelected) {
                var r: Float
                var g: Float
                var b: Float
                var col: Color
                for (residue in pdbModel.residues) {
                    if (residue.secondaryStructure != null) {
                        if (residue.secondaryStructure?.secondaryStructureType == SecondaryStructure.StructureType.alphahelix) {
                            col = Color.RED
                        } else {
                            col = Color.CORNFLOWERBLUE
                        }
                    } else {
                        r = randomGenerator.nextFloat()
                        g = randomGenerator.nextFloat()
                        b = randomGenerator.nextFloat()
                        col = Color(r.toDouble(), g.toDouble(), b.toDouble(), 1.0)
                    }

                    residue.cBetaAtom?.colorProperty?.setValue(col)
                    residue.cAlphaAtom?.colorProperty?.value = col
                    residue.nAtom?.colorProperty?.value = col
                    residue.cAtom?.colorProperty?.value = col
                    residue.oAtom?.colorProperty?.value = col
                    for (bond in pdbModel.getBondsOfResidue(residue)) {
                        world.getEdgeByModel(bond).colorProperty().setValue(col)
                    }
                }
            }
        }
    }

    /**
     * Set up the transformation property for rotating the graph
     */
    private fun setUpTransforms() {
        worldTransformProperty.addListener { e, o, n -> world.getTransforms().setAll(n) }
    }

    /**
     * Set up the perspective camera showing the subScene3d.
     */
    private fun setUpPerspectiveCamera() {
        val perspectiveCamera = PerspectiveCamera(true)
        perspectiveCamera.nearClip = 0.1
        perspectiveCamera.farClip = PANEDEPTH * 2
        perspectiveCamera.translateZ = -PANEDEPTH / 2
        this.subScene3d.camera = perspectiveCamera
    }

    /**
     * Set up listeners on the model in order to update the view's representation of it.
     */
    private fun setUpModelListeners() {
        pdbModel.edgesProperty().addListener(ListChangeListener<Bond> { c ->
            while (c.next()) {
                // Handle added edges
                if (c.wasAdded())
                    c.addedSubList.forEach { myEdge -> world.addEdge(myEdge) }
                // Handle removed edges
                if (c.wasRemoved())
                    c.removed.forEach { myEdge -> world.removeEdge(myEdge) }
            }
        })
        pdbModel.nodesProperty().addListener(ListChangeListener<Atom> { c ->
            while (c.next()) {
                // Add nodes
                if (c.wasAdded()) {
                    c.addedSubList.forEach { myNode -> world.addNode(myNode) }
                }
                // Remove nodes
                if (c.wasRemoved())
                    c.removed.forEach { myNode -> world.removeNode(myNode) }
            }
        })

        pdbModel.secondaryStructuresProperty().addListener(ListChangeListener<SecondaryStructure> { c ->
            while (c.next()) {
                // Add structure
                if (c.wasAdded()) {
                    c.addedSubList
                        .forEach { secondaryStructure -> world.addSecondaryStructure(secondaryStructure) }
                }
                // Remove structure
                if (c.wasRemoved()) {
                    c.removed.forEach { secondaryStructure -> world.removeSecondaryStructure(secondaryStructure) }
                }
            }
        } as ListChangeListener<SecondaryStructure>)
    }

    /**
     * Set the relationship of menu options to the graph's state and to buttons. If there are no nodes in the graph, no
     * graph can be saved or reset.
     */
    private fun setMenuItemRelations() {
        // Set to true if number of nodes is zero, or an animation is running
        val disableButtons = Bindings.equal(0, Bindings.size(pdbModel.nodesProperty())).or(animationRunning)

        // Either show atoms or show bonds or both are active -> true. Else false
        val showAtomsOrBonds =
            Bindings.or(view.showAtomsMenuItem.selectedProperty(), view.showBondsMenuItem.selectedProperty()).not()

        val disableAtomViewControls = view.atomViewMenuItem.selectedProperty().not()

        //Disable everything, when no file was loaded yet.
        view.runBlastButton.disableProperty().bind(disableButtons)
        view.toolBar.disableProperty().bind(disableButtons)
        view.lowerToolBar.disableProperty().bind(disableButtons)
        view.fileMenu.disableProperty().bind(animationRunning)
        view.editMenu.disableProperty().bind(disableButtons)
        view.viewMenu.disableProperty().bind(disableButtons)

        // Can only show/hide CBeta if either bonds or atoms or both are shown
        view.showCBetaMenuItem.disableProperty()
            .bind(Bindings.and(showAtomsOrBonds, Bindings.not(disableAtomViewControls)))
        view.showCBetaToolBarButton.disableProperty().bind(view.showCBetaMenuItem.disableProperty())

        // make the sliders for scaling only available in atom/bond view, since they make no sense for cartoon
        view.scaleEdgesSlider.disableProperty().bind(disableAtomViewControls)
        view.scaleNodesSlider.disableProperty().bind(disableAtomViewControls)
        view.scaleEdgesLabel.disableProperty().bind(disableAtomViewControls)
        view.scaleNodesLabel.disableProperty().bind(disableAtomViewControls)

        view.showRibbonMenuItem.disableProperty().bind(disableAtomViewControls)
        view.showBondsMenuItem.disableProperty().bind(disableAtomViewControls)
        view.showAtomsMenuItem.disableProperty().bind(disableAtomViewControls)
        view.showCBetaMenuItem.disableProperty().bind(disableAtomViewControls)

        view.coloringByElementMenuItem.disableProperty().bind(disableAtomViewControls)
        view.coloringBySecondaryMenuItem.disableProperty().bind(disableAtomViewControls)
        view.coloringByResidueMenuItem.disableProperty().bind(disableAtomViewControls)

        // Bind worlds radius scaling properties to the sliders in the view
        world.bondRadiusScalingProperty().bind(view.scaleEdgesSlider.valueProperty())
        world.atomRadiusScalingProperty().bind(view.scaleNodesSlider.valueProperty())

        view.lowerToolBar.managedProperty().bind(view.lowerToolBar.visibleProperty())
        view.lowerToolBar.visibleProperty().bind(Bindings.not(disableAtomViewControls))

        //Set initial values
        resetSettings()
    }

    private fun setUpTabPane() {
        view.contentTabPane.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
    }

    /**
     * Set actions for clicking on MenuItems in the edit menu.
     */
    private fun setEditMenuActions() {
        // Clear graph action
        view.clearGraphMenuItem.setOnAction { event ->
            resetSettings()
            animationRunning.value = true
            selectionModel.clearSelection()
            // reset node connecting cache
            // scale to 0
            val scaleTransition = ScaleTransition(Duration.seconds(1.0), world)
            scaleTransition.toX = 0.0
            scaleTransition.toY = 0.0
            scaleTransition.toZ = 0.0
            // fade to 0
            val fadeTransition = FadeTransition(Duration.seconds(1.0), world)
            fadeTransition.toValue = 0.0
            // run in parallel
            val parallelTransition = ParallelTransition(scaleTransition, fadeTransition)
            parallelTransition.play()
            // when done reset opacity and scale properties and delete graph's contents (nodes and edges)
            parallelTransition.setOnFinished { finishedEvent ->
                pdbModel.reset()
                worldTransformProperty.setValue(Rotate())
                world.opacity = 1.0
                world.scaleX = 1.0
                world.scaleY = 1.0
                world.scaleZ = 1.0
                animationRunning.value = false
            }
            event.consume()
        }

        view.resetRotationMenuItem.setOnAction { event -> worldTransformProperty.setValue(Rotate()) }

        //Blast service and MenuItems are set up in setUpBlastService()
    }

    /**
     * Reset the view settings when loading a new graph or dismissing the previously loaded one.
     */
    private fun resetSettings() {
        view.coloringByElementMenuItem.selectedProperty().value = true
        view.showAtomsMenuItem.selectedProperty().value = true
        view.showBondsMenuItem.selectedProperty().value = true
        view.showCBetaMenuItem.selectedProperty().value = true
        view.atomViewMenuItem.selectedProperty().value = true
        view.showRibbonMenuItem.selectedProperty().value = false
        MyRibbonView3D.reset()
        view.secondaryStructureContentStackedBarChart.reset()
    }

    /**
     * Set actions for the open and save options in the file menu.
     */
    private fun setFileMenuActions() {
        view.loadFileMenuItem.setOnAction { event ->

            // If BLAST service is running ask user if it should be aborted for loading a file. If has already run reset it.
            if (abortLoadBecauseOfBlastService()) return@setOnAction
            view.tgfFileChooser.extensionFilters.add(
                FileChooser.ExtensionFilter(
                    "PDB files (.pdb, .PDB)",
                    "*.pdb", "*.PDB"
                )
            )
            val graphFile = view.tgfFileChooser.showOpenDialog(primaryStage)
            try {
                val pdbFile = BufferedReader(InputStreamReader(FileInputStream(graphFile)))
                loadNewPDBFile(pdbFile)
            } catch (e: IOException) {
                System.err.println(e.message)
            } catch (e: NullPointerException) {
                println("No file chosen. Aborted.")
            }
        }

        // Easy loading of all three PDB files
        view.open2TGAMenuItem.setOnAction { event ->
            // If BLAST service is running ask user if it should be aborted for loading a file. If has already run reset it.
            if (abortLoadBecauseOfBlastService()) return@setOnAction
            // Load file from resources
            val pdbFile = BufferedReader(InputStreamReader(javaClass.getResourceAsStream("/2tga.pdb")))
            loadNewPDBFile(pdbFile)
        }

        view.open2KL8MenuItem.setOnAction { event ->
            // If BLAST service is running ask user if it should be aborted for loading a file. If has already run reset it.
            if (abortLoadBecauseOfBlastService()) return@setOnAction
            // Load file from resources
            val pdbFile = BufferedReader(InputStreamReader(javaClass.getResourceAsStream("/2kl8.pdb")))
            loadNewPDBFile(pdbFile)
        }

        view.open1EY4MenuItem.setOnAction { event ->
            // If BLAST service is running ask user if it should be aborted for loading a file. If has already run reset it.
            if (abortLoadBecauseOfBlastService()) return@setOnAction
            // Load file from resources
            val pdbFile = BufferedReader(InputStreamReader(javaClass.getResourceAsStream("/1ey4.pdb")))
            loadNewPDBFile(pdbFile)
        }
    }

    /**
     * Is used to check the status of the Blast service before a new file is loaded. If is in any finished state
     * (succeeded, cancelled, etc) then it is reset, if it is in the running state (scheduled, running) then the
     * user is queried on how to proceed.
     *
     * @return True if the Blast service should be aborted, false else.
     */
    private fun abortLoadBecauseOfBlastService(): Boolean {
        if (!blastService.isRunning) {
            // If the service is run a second time, reset its state.
            if (blastService.state == Worker.State.CANCELLED ||
                blastService.state == Worker.State.FAILED ||
                blastService.state == Worker.State.READY ||
                blastService.state == Worker.State.SUCCEEDED
            ) {
                blastService.reset()
            }
            return false
        } else {
            // BLAST is still running. Show a confirmation alert if BLAST should really be aborted
            val alert = Alert(Alert.AlertType.CONFIRMATION)
            alert.title = "BLAST service running"
            alert.contentText =
                "Blast service is still running.\n" + "Do you really want to cancel and load a new file?"
            val result = alert.showAndWait()

            if (result.isPresent && result.get() == ButtonType.OK) {
                // Abort BLASTing
                blastService.cancel()
                blastService.reset()
                return false
            } else {
                // Cancel loading new file and continue blasting
                return true
            }
        }
    }

    /**
     * Load a new PDB model from the provided file. This replaces already loaded data, but does not destroy
     * listeners on view or presenter, but on single nodes and edges, since previously loaded data are destroyed.
     *
     * @param inputStreamReader The PDB file to be loaded.
     */

    private fun loadNewPDBFile(inputStreamReader: BufferedReader?) {
        // Report error
        if (inputStreamReader == null) {
            System.err.println("No file chosen. Model not touched")
            return
        }

        try {
            resetSettings()
            resetBLASTResult()
            worldTransformProperty.setValue(Rotate())
            pdbModel.reset()
            // parse the file and set up the model. The view listens to the model and handles everything else automatically
            PDBParser.parse(pdbModel, inputStreamReader)
            // set the new selection model
            val residues = pdbModel.residues
            info{"New residues are $residues"}
            selectionModel.items = residues.toList()
            // Compute charts
            view.secondaryStructureContentStackedBarChart.initialize(
                pdbModel.alphaHelixContent,
                pdbModel.betaSheetContent,
                pdbModel.coilContent,
                view.contentTabPane.widthProperty(),
                view.contentTabPane.heightProperty()
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    /**
     * Clear the BLAST result tab, when settings are reset, due to e.g. a new file being loaded.
     */
    private fun resetBLASTResult() {
        cancelBlast(true)
        view.blastText.text = ""
    }


    /**
     * Class to connect/bind the stats of the model with their value and a proper label.
     */
    inner class StatViewerBinding internal constructor(
        internal var bindingLabel: String,
        internal var p: IntegerBinding
    ) : StringBinding() {

        init {
            super.bind(p)
        }

        override fun computeValue(): String {
            // Return nice String format
            return bindingLabel + p.value!!.toString()
        }
    }

    /**
     * Bind the statistics label to the number of nodes and edges.
     */
    private fun initializeStatsBindings() {
        // Create a String binding of the label to the size of the nodes list
        val stringBindingNodes = StatViewerBinding("# atoms: ", Bindings.size(pdbModel.nodesProperty()))
        view.numberOfNodesLabel.textProperty().bind(stringBindingNodes)

        // Create a String binding of the label to the size of the edges list
        val stringBindingEdges = StatViewerBinding("# bonds: ", Bindings.size(pdbModel.edgesProperty()))
        view.numberOfEdgesLabel.textProperty().bind(stringBindingEdges)
    }


    /**
     * Set up the view's nodes and edges to be click- and moveable.
     *
     * @param node The node to be registered.
     */
    fun setUpNodeView(node: MyNodeView3D) {
        node.setOnMouseClicked { event ->
            if (event.getButton() == MouseButton.PRIMARY) {
                val clickedResidue = node.modelNodeReference.residueProperty.value
                selectInSelectionModel(clickedResidue, event)
            }
            event.consume()
        }
    }

    /**
     * Set up mouse events on 3D graph group.
     */
    private fun setUpMouseEventListeners() {
        // This is dragging the graph and rotating it around itself.
        view.bottomPane.setOnMouseDragged { event ->

            val deltaX = event.sceneX - pressedX
            val deltaY = event.sceneY - pressedY

            // Get the perpendicular axis for the dragged point
            val direction = Point3D(deltaX, deltaY, 0.0)
            val axis = direction.crossProduct(0.0, 0.0, 1.0)
            val angle = 0.4 * sqrt(deltaX.pow(2.0) + deltaY.pow(2.0))

            //compute the main focus of the world and use it as pivot
            val focus = computePivot()

            val rotation = Rotate(angle, focus.x, focus.y, focus.z, axis)

            // Apply the rotation as an additional transform, keeping earlier modifications
            worldTransformProperty.value = rotation.createConcatenation(worldTransformProperty.value)

            // Set the variables new
            pressedX = event.sceneX
            pressedY = event.sceneY

            event.consume()
            // Reset source node, if the adding of an edge was previously initiated
        }

        // Save the coordinates, in order to support the dragging of the graph (rotation)
        view.bottomPane.setOnMousePressed { event ->
            pressedX = event.sceneX
            pressedY = event.sceneY
            event.consume()
        }

        // Implement zooming, when scolling with the mouse wheel or on a trackpad
        view.bottomPane.setOnScroll { event ->
            val delta = 0.01 * event.deltaY + 1
            val focus = computePivot()
            val scale = Scale(delta, delta, delta, focus.x, focus.y, focus.z)
            worldTransformProperty.setValue(scale.createConcatenation(worldTransformProperty.value))
        }
    }

    /**
     * Sets up the sequence pane, holding the whole sequence, which should be clickable and bound to the SelectionModel.
     * Sets up listeners on the selection model, in order to mark selected residues in both the sequence and the
     * atom/bond view graph.
     */
    private fun setUpSequencePaneAndSelectionModel() {
        pdbModel.residues.addListener(ListChangeListener<Residue> { c ->
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach { residue ->
                        val vbox =
                            view.addResidueToSequence(
                                residue.oneLetterAminoAcidName,
                                residue.oneLetterSecondaryStructureType
                            )
                        // Handle clicks on the vbox representing a residue -> selection
                        vbox.setOnMouseClicked { event ->
                            if (event.button == MouseButton.PRIMARY) {
                                selectInSelectionModel(residue, event)
                            }
                            event.consume()
                        }
                        // This will also set up the ribbon for all residues in the view
                        world.addResidue(residue)
                    }
                } else if (c.wasRemoved()) {
                    // Remove residue from the pane showing the sequence and secondary structure.
                    view.sequenceFlowPane.children.remove(c.from, c.to + c.removedSize)
                    // This will destroy residues from ribbon view
                    c.removed.forEach { residue -> world.removeResidue(residue) }
                }
            }
        })

        // deselect everything if the pane and not a residue was clicked.
        view.sequenceScrollPane.setOnMouseClicked { event ->
            // only if control/cmd is not pressed
            if (!event.isMetaDown)
                selectionModel.clearSelection()
        }

        // deselect everything if the pane and not a residue was clicked.
        //        view.bottomPane.setOnMouseClicked(event -> {
        //            // only if control/cmd is not pressed
        //            if(!event.isMetaDown())
        //                selectionModel.clearSelection();
        //
        //        });

        // Mark selected residues in the sequence pane. This removes and adds markings depending
        // on the selection model's state
        selectionModel.getSelectedIndices().addListener(ListChangeListener<Int> { c ->
            while (c.next()) {
                if (c.wasAdded()) {
                    c.addedSubList.forEach { index ->
                        (view.sequenceFlowPane.children[index.toInt()] as VBox).background = Background(
                            BackgroundFill(
                                Color.CORNFLOWERBLUE, CornerRadii.EMPTY,
                                Insets(0.0)
                            )
                        )
                    }
                }
                if (c.wasRemoved()) {
                    try {
                        c.removed.forEach { index ->
                            (view.sequenceFlowPane.children[index.toInt()] as VBox).background = Background.EMPTY
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        // This can happen when there are residues marked and the c beta atoms are not shown. Only when
                        // new file is loaded and the list is cleared, this can happen, due to setting a listener on
                        // the showCBeta property
                        // See issue #1. This does not affect the applications functionality, after the list was cleared.
                    }

                }
            }
        } as ListChangeListener<Int>)

        // Mark selected residues in the graph's atom/bond view. This adds and removes bounding
        // boxes for each residue which is marked
        selectionModel.selectedItems.addListener(ListChangeListener<Residue> { c ->
            while (c.next()) {
                if (c.wasAdded()) {
                    c.addedSubList.forEach { residue ->
                        // Find the nodes
                        val calpha = world.getNodeByModel(residue.cAlphaAtom!!)
                        val cbeta = world.getNodeByModel(residue.cBetaAtom!!)
                        val catom = world.getNodeByModel(residue.cAtom!!)
                        val n = world.getNodeByModel(residue.nAtom!!)
                        val o = world.getNodeByModel(residue.oAtom!!)

                        // Create a group of bounding boxes for the residue and add it to the topPane
                        val resGroup = Group()
                        val bbca = BoundingBox2D(view.bottomPane, calpha, worldTransformProperty, subScene3d)
                        val bbcb = BoundingBox2D(view.bottomPane, cbeta, worldTransformProperty, subScene3d)
                        // issue #2 fixed not showing bounding box if c beta are hidden
                        view.showCBetaToolBarButton.selectedProperty()
                            .addListener(WeakInvalidationListener { observable ->
                                bbcb.visibleProperty().setValue(view.showCBetaToolBarButton.isSelected)
                            })
                        val bbc = BoundingBox2D(view.bottomPane, catom, worldTransformProperty, subScene3d)
                        val bbn = BoundingBox2D(view.bottomPane, n, worldTransformProperty, subScene3d)
                        val bbo = BoundingBox2D(view.bottomPane, o, worldTransformProperty, subScene3d)
                        resGroup.children.addAll(bbca, bbcb, bbc, bbn, bbo)

                        view.topPane.children.add(resGroup)
                    }
                }
                if (c.wasRemoved()) {
                    // Remove the correct element (Group of nodes) from the scene graph when unselected.
                    view.topPane.children.remove(c.from, c.to + c.removedSize)
                }
            }
        } as ListChangeListener<Residue>)
    }

    /**
     * Choose whether to select or unselect a clicked residue, when control/cmd is down.
     * When control/cmd isn't down, the selection is cleared and the element is selected.
     *
     * @param r     The selected residue.
     * @param event The mouse event which triggered the action.
     */
    private fun selectInSelectionModel(r: Residue, event: MouseEvent) {
        if (selectionModel.isSelected(r)) {
            // The clicked residue is already selected
            if (event.isMetaDown) {
                // if control/cmd is down we want to deselect the clicked item, but not all. So we only unselect the clicked one
                selectionModel.clearSelection(r)
            } else {
                // if control/cmd is not down and the clicked item was already selected, we want to unselect it, if it is the only selected item.
                if (selectionModel.getSelectedItems().size == 1) {
                    selectionModel.clearSelection()
                } else {
                    // if the clicked item is not the only selected item, we clear the selection and only select the clicked item
                    selectionModel.clearAndSelect(r)
                }
            }
        } else {
            // The clicked residue is not yet selected.
            if (event.isMetaDown) {
                // If control/cmd is pressed, we allow to select multiple
                selectionModel.select(r)
            } else {
                // if control/cmd is not pressed we clear the selection and select the clicked item
                selectionModel.clearAndSelect(r)
            }
        }
    }

    /**
     * Compute the focus of the world in order to have a pivot for the rotation axis of the world.
     *
     * @return Focus of the world.
     */
    private fun computePivot(): Point3D {
        // Use the local bound for computation of the midpoint
        val b = world.boundsInLocal
        val x = b.maxX - b.width / 2
        val y = b.maxY - b.height / 2
        val z = b.maxZ - b.depth / 2
        return Point3D(x, y, z)
    }
}
