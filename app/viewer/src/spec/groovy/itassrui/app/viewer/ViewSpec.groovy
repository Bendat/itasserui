package itassrui.app.viewer

import itasserui.app.viewer.pdbmodel.PDBEntry
import itasserui.app.viewer.pdbmodel.PDBParser
import itasserui.app.views.renderer.atom.node.NodeRenderer
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.Scene
import javafx.stage.Stage
import org.testfx.framework.spock.ApplicationSpec

class ViewSpec extends ApplicationSpec {
    def pdb = this.class.getResource('/1ey4.pdb').file

    @Override
    void start(Stage stage) throws Exception {
        def entry = new PDBEntry()
        def br = new BufferedReader(new FileReader(pdb))
        PDBParser.INSTANCE.parse(entry, br)
        print(entry.allCBetaAtoms)
        def view = new NodeRenderer(entry.allCBetaAtoms[0], new SimpleDoubleProperty(1.0))
        def scene = new Scene(view.root)
        stage.scene = scene
        stage.show()

    }

    void stall(){
        expect:
        Thread.sleep(15000)
    }
}