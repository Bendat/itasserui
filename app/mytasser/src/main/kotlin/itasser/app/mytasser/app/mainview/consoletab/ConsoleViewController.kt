package itasser.app.mytasser.app.mainview.consoletab

import itasser.app.mytasser.app.process.pane.widget.ProcessWidgetController
import itasserui.app.mytasser.lib.kInject
import itasserui.app.user.ProfileManager
import itasserui.lib.process.details.ProcessOutput
import itasserui.lib.process.process.ITasser
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.image.Image
import tornadofx.*

class SelectedSequenceEvent(val sequence: ITasser) : FXEvent()
class EventShooter(override val scope: Scope) : View("Event Shootr") {
    override val root = vbox { }
}

class ConsoleViewController : Controller() {
    val profileManager: ProfileManager by kInject()
    val selectedSequenceProperty: SimpleObjectProperty<ITasser?> = SimpleObjectProperty()
    var selectedSequence: ITasser? by selectedSequenceProperty
    val commandProperty = SimpleObjectProperty<String>("No sequence run yet")
    var command by commandProperty
    val stdStream: ObservableList<ProcessOutput> = FXCollections.observableArrayList()
    val runStopIcons =
        ProcessWidgetController.PlayPauseIcons(resources.image("/icons/play.png"), resources.image("/icons/pause.png"))
    val runPauseIconProperty = SimpleObjectProperty(runStopIcons.play)
    var runPauseIcon: Image by runPauseIconProperty

    val stopIconProperty = SimpleObjectProperty(resources.image("/icons/stop.png"))
    var stopIcon by stopIconProperty

    val copyIcon = SimpleObjectProperty(resources.image("/icons/copy.png"))

    init {
        subscribe<SelectedSequenceEvent> {
            selectedSequence = it.sequence
        }

        selectedSequenceProperty.onChange {
            it?.let { itasser ->
                stdStream.clear()
                command = itasser.command
                itasser.std.stream.onAdd += { item, index -> Platform.runLater { stdStream.add(item) } }
            }
        }
    }
}

class ConsoleViewModel : ItemViewModel<ConsoleViewController>(ConsoleViewController()) {
    val selectedSequence = bind(ConsoleViewController::selectedSequenceProperty)
    val command = bind(ConsoleViewController::commandProperty)
    val stream = bind(ConsoleViewController::stdStream)
    val runPauseIcon = bind(ConsoleViewController::runPauseIconProperty)
    val stopIcon = bind(ConsoleViewController::stopIconProperty)
    val copyIcon = bind(ConsoleViewController::copyIcon)
    fun perform(ifNot: () -> Unit, op: (ITasser) -> Unit) {
        item.selectedSequence?.let { seq ->
            item.profileManager.perform(seq.process.createdBy, ifNot) { op(seq) }
        }
    }
}


