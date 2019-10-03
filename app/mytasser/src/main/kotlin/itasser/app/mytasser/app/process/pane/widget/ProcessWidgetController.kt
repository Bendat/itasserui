package itasser.app.mytasser.app.process.pane.widget

import itasser.app.mytasser.lib.extensions.bind
import itasserui.app.user.User
import itasserui.common.logger.Logger
import itasserui.lib.process.details.ExecutionState
import itasserui.lib.process.details.ExecutionState.Queued
import itasserui.lib.process.details.ExecutionState.Running
import itasserui.lib.process.process.ITasser
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue

class ProcessWidgetController(
    val user: User,
    val itasser: ITasser
) : Controller(), Logger {

    val runStopIcons = PlayPauseIcons(resources.image("/icons/play.png"), resources.image("/icons/pause.png"))

    val usernameProperty = SimpleStringProperty(user.username.value)
    var username: String by usernameProperty

    val executionStateProperty = SimpleObjectProperty<ExecutionState>(Queued)
        .bind(itasser.stateProperty)

    val startedTimeProperty = SimpleObjectProperty<Long>(0L)
        .bind(itasser.startedTimeProperty)

    var startedTime: Long by startedTimeProperty

    init {
        startedTimeProperty.addListener { it1, it2, it3 ->
            info { "Started time changed from $it3 to $it2" }
        }
    }

    val executionTimeProperty = SimpleObjectProperty(0L)
        .bind(itasser.executionTimeProperty)
    val sequenceNameProperty = SimpleStringProperty(itasser.process.name)
    var sequenceName: String by sequenceNameProperty

    val runPauseIconProperty = SimpleObjectProperty(runStopIcons.play)
    var runPauseIcon: Image by runPauseIconProperty

    val stopIconProperty = SimpleObjectProperty(resources.image("/icons/stop.png"))
    var stopIcon by stopIconProperty

    fun setRunPlayIcon(state: ExecutionState) {
        runPauseIcon = when (state) {
            is Running -> runStopIcons.pause
            else -> runStopIcons.play
        }
    }

    data class PlayPauseIcons(
        val play: Image,
        val pause: Image
    )
}