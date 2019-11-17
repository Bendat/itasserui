package itasser.app.mytasser.app.mainview.consoletab

import itasser.app.mytasser.app.events.SelectedSequenceEvent
import itasser.app.mytasser.app.process.pane.widget.ProcessWidgetController
import itasser.app.mytasser.lib.kInject
import itasserui.app.user.ProfileManager
import itasserui.lib.process.details.ExecutionState
import itasserui.lib.process.details.ProcessOutput
import itasserui.lib.process.process.ITasser
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.image.Image
import lk.kotlin.observable.property.plusAssign
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.onChange
import tornadofx.setValue


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
    val executionStateProperty = SimpleObjectProperty<ExecutionState>(ExecutionState.Queued)
    val stopIconProperty = SimpleObjectProperty(resources.image("/icons/stop.png"))
    var stopIcon by stopIconProperty

    val copyIcon = SimpleObjectProperty(resources.image("/icons/copy.png"))
    fun setRunPlayIcon(state: ExecutionState) {
        runPauseIcon = when (state) {
            is ExecutionState.Running -> runStopIcons.pause
            else -> runStopIcons.play
        }
    }

    init {
        subscribe<SelectedSequenceEvent> {
            selectedSequence = it.sequence
            it.sequence.stateProperty += { prop->
                if(selectedSequence == it.sequence)
                    executionStateProperty.value = it.sequence.state
            }
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




