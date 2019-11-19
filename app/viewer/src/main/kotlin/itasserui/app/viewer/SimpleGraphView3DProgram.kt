package itasserui.app.viewer

import itasserui.app.viewer.pdbmodel.PDBEntry
import itasserui.app.viewer.view.Presenter
import itasserui.app.viewer.view.View
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

import javax.swing.ImageIcon
import java.awt.*
import java.net.URL

/**
 * Main class.
 *
 * @author Patrick Grupp
 */
class SimpleGraphView3DProgram : Application() {

    override fun start(primaryStage: Stage) {
        // Create the GUI view and a model graph.
        val view = View()
        val graph = PDBEntry()


        // Set the scene and show it
        val scene = Scene(view)
        primaryStage.setScene(scene)

        // The presenter handles connecting/updating view and model
        Presenter(view, graph, primaryStage)
        primaryStage.title = "PDB Viewer"
        // Set Application icon
        val icon =
            javafx.scene.image.Image(SimpleGraphView3DProgram::class.java!!.getResourceAsStream("/pdb_viewer.png"))
        primaryStage.icons.add(icon)
        try {
            val iconURL = SimpleGraphView3DProgram::class.java!!.getResource("/pdb_viewer.png")
            val ico = ImageIcon(iconURL).getImage()
            com.apple.eawt.Application.getApplication().setDockIconImage(ico)
        } catch (e: Exception) {
            // Mac stuff won't work on Windows or Linux, so just ignore it.
        }

        primaryStage.show()
    }

}
