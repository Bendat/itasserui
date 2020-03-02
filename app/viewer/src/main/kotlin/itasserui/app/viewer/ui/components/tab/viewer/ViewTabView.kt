package itasserui.app.viewer.ui.components.tab.viewer

import itasserui.app.viewer.footer.FooterView
import itasserui.app.viewer.renderer.components.canvas.CanvasView
import itasserui.app.viewer.ui.components.tab.viewer.toolbars.PDBAppearanceToolbarView
import itasserui.app.viewer.ui.components.tab.viewer.toolbars.PDBScalingToolbarView
import tornadofx.*
import javafx.scene.control.TabPane

class ViewTabView : View("My View") {

    override val root =
        borderpane {
            center {
                add<CanvasView> {
                    root.minWidth = 10.0
                    root.minHeight = 10.0
                }
            }

            top {
                vbox {
                    add<PDBAppearanceToolbarView>()
                    add<PDBScalingToolbarView>()
                    add<FooterView>()
                }

            }
        }
    }


