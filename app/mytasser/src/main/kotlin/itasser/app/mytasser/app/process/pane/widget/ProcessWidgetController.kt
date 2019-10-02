package itasser.app.mytasser.app.process.pane.widget

import itasser.app.mytasser.lib.DI
import itasserui.app.user.User
import itasserui.lib.process.process.ITasser
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import org.joda.time.DateTime
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue

class ProcessWidgetController(val user: User, val itasser: ITasser) : Controller() {
    val kdi: DI by inject()
    private val playPauseIcons = mapOf(
        0 to resources.image("/icons/play.png"),
        1 to resources.image("/icons/pause.png")
    )

    val usernameProperty = SimpleStringProperty(user.username.value)
    var username: String by usernameProperty



    val startedTimeProperty = SimpleLongProperty(System.currentTimeMillis())
    var startedTime: Long by startedTimeProperty

    val startedTimeFormattedProperty = SimpleStringProperty(DateTime.now().toLocalTime().toString("HH:mm"))
    var startedTimeFormatted: String by startedTimeFormattedProperty

    val sequenceIconProperty = SimpleObjectProperty(resources.image("/icons/dna.png"))
    var sequenceIcon: Image by sequenceIconProperty

    val sequenceNameProperty = SimpleStringProperty(itasser.process.name)
    var sequenceName: String by sequenceNameProperty

    val runPauseIconProperty = SimpleObjectProperty(playPauseIcons.getValue(0))
    var runPauseIcon: Image by runPauseIconProperty

    val stopIconProperty = SimpleObjectProperty(resources.image("/icons/stop.png"))
    var stopIcon by sequenceIconProperty

    fun onRunPauseClicked() {
        runPauseIcon = when (runPauseIcon) {
            playPauseIcons[0] -> playPauseIcons[1] ?: error("")
            else -> playPauseIcons[0] ?: error("")
        }
    }

}