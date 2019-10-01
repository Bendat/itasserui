package itasser.app.mytasser.app.process.pane.widget

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import org.joda.time.DateTime
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue

class ProcessWidgetController : Controller() {
    val userIconProperty = SimpleObjectProperty(resources.image("/icons/users.png"))
    var userIcon: Image by userIconProperty

    val usernameProperty = SimpleStringProperty("user.name")
    var username: String by usernameProperty

    val startedTimeIconProperty = SimpleObjectProperty(resources.image("/icons/clock.png"))
    var startedTimeIcon: Image by startedTimeIconProperty

    val startedTimeProperty = SimpleStringProperty(DateTime.now().toLocalTime().toString("HH:mm"))
    var startedTime: String by startedTimeProperty

    val sequenceIconProperty = SimpleObjectProperty(resources.image("/icons/dna.png"))
    var sequenceIcon: Image by sequenceIconProperty

    val sequenceNameProperty = SimpleStringProperty("/sequence.name")
    var sequenceName: String by sequenceNameProperty

    val runPauseIconProperty = SimpleObjectProperty(resources.image("/icons/play.png"))
    var runPauseIcon: Image by runPauseIconProperty

    val stopIconProperty = SimpleObjectProperty(resources.image("/icons/stop.png"))
    var stopIcon by sequenceIconProperty



}