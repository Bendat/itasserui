package itasser.app.mytasser.app.process.pane

import itasser.app.mytasser.app.process.pane.widget.ProcessWidget
import itasserui.app.mytasser.lib.DI
import itasserui.lib.process.process.ITasser
import javafx.scene.control.ListView
import javafx.scene.control.ScrollPane
import tornadofx.*

class ProcessPane(di: DI, scope: Scope? = null) : View("My View") {
    override val scope: Scope = scope ?: super.scope
    val model: ProcessPaneViewModel by inject()

    init {
        setInScope(di, this.scope)
    }

    override val root = scrollpane {
        prefHeight = 500.0
        prefWidth = 250.0
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        squeezebox {
            fold("Completed") {
                addClass("completed-fold")
                listview(model.completed) {
                    widget()
                }
            }

            fold("Failed") {
                addClass("failed-fold")
                listview(model.failed) {
                    widget()
                }
            }

            fold("Runnning") {
                addClass("running-fold")
                listview(model.running) {
                    widget()
                }
            }

            fold("Queued") {
                addClass("queued-fold")
                listview(model.queued) {
                    widget()
                }
            }
        }
    }

    private fun ListView<ITasser>.widget() {
        return cellFormat { process ->
            val user = model.profileManager.find(process.process.createdBy).user
            graphic = ProcessWidget(user, process).root
        }
    }
}
