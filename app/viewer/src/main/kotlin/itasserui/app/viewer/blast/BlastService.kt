package itasserui.app.viewer.blast

import itasserui.app.viewer.events.*
import itasserui.app.viewer.renderer.components.graph.GraphView
import itasserui.common.extensions.isNull
import itasserui.lib.pdb.parser.PDB
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Alert
import javafx.util.StringConverter
import tornadofx.*
import java.util.*
import kotlin.concurrent.timer

@Suppress("MemberVisibilityCanBePrivate", "unused")
class BlastServiceController : Controller() {
    val sequenceCodeProperty = "".toProperty()
    var sequenceCode: String by sequenceCodeProperty

    val programProperty = SimpleObjectProperty<BlastProgram>(BlastProgram.blastp)
    var program: BlastProgram by programProperty

    val statusProperty = SimpleObjectProperty<BlastStatus>(BlastStatus.Idle)
    var status: BlastStatus by statusProperty

    val databaseProperty = "nr".toProperty()
    var database: String by databaseProperty

    val requestIdProperty = "".toProperty()
    var requestId: String by requestIdProperty

    val startTimeProperty = Long.MIN_VALUE.toProperty()
    var startTime: Long by startTimeProperty

    val estimatedTimeProperty = Long.MIN_VALUE.toProperty()
    var estimatedTime: Long by estimatedTimeProperty

    val actualTimeProperty = Long.MIN_VALUE.toProperty()
    var actualTime: Long by actualTimeProperty

    init {
        subscribe<PDBLoadedEvent> {
            sequenceCode = it.pdb.header.code
        }
        subscribe<BlastClient.BlastDetailsEvent> {
            requestId = it.requestID
            estimatedTime = it.timeEstimate
        }
        subscribe<BlastStatusEvent> {
            status = it.status
        }
    }
}

class BlastServiceModel : ItemViewModel<BlastServiceController>() {
    val sequenceCode = bind(BlastServiceController::sequenceCodeProperty)
    val program = bind(BlastServiceController::programProperty)
    val status = bind(BlastServiceController::statusProperty)
    val database = bind(BlastServiceController::databaseProperty)
    val requestId = bind(BlastServiceController::requestIdProperty)
    val startTime = bind(BlastServiceController::startTimeProperty)
    val estimatedTime = bind(BlastServiceController::estimatedTimeProperty)
    val actualTime = bind(BlastServiceController::actualTimeProperty)
}


class BlastServiceView : View() {
    val controller: BlastServiceController by inject()
    val client: BlastClient by inject()
    val model: BlastServiceModel by inject()
    val graph: GraphView by inject()

    init {
        model.item = controller
    }

    override val root = vbox {
        subscribe<BlastAlreadyRunningEvent> {
            alert(Alert.AlertType.INFORMATION,
                "Blast is Already Running",
                "You can run it again once the current execution is finished or cancelled.")
        }
        toolbar {
            button("Run BLAST") {
                setOnAction {
                    runBlastActon()
                }
            }

            button("Cancel Blast") {
                setOnAction {
                    fire(BlastEndedEvent)
                }
            }
            separator()
            label(model.status, converter = statusConverter())
            val idSeperator = separator { isVisible = false }
            label(model.requestId, converter = idConverter()) {
                idSeperator.visibleProperty().bind(visibleProperty())
                isVisible = false
                controller.requestIdProperty.onChange {
                    isVisible = it != ""
                }
            }
            val timeSeparator = separator { isVisible = false }
            label(model.estimatedTime, converter = timeConverter("Est. Time")) {
                timeSeparator.visibleProperty().bind(visibleProperty())
                isVisible = false
                controller.estimatedTimeProperty.onChange {
                    isVisible = it >= 0
                }
            }
            val actualSeparator = separator { isVisible = false }
            label(model.actualTime, converter = timeConverter("Elapsed Time")) {
                actualSeparator.visibleProperty().bind(visibleProperty())
                isVisible = false
                controller.actualTimeProperty
                    .onChange { isVisible = it >= 0 }
                var timer: Timer? = null
                subscribe<BlastStartedEvent> {
                    controller.actualTime = 0
                    timer = timer("Blast execution time", false, 0L, 1000L) {
                        Platform.runLater { controller.actualTime += 1 }
                    }
                }

                subscribe<BlastEndedEvent> {
                    timer?.cancel()
                    timer?.purge()
                }
            }
        }
    }

    private fun runBlastActon() {
        if (graph.controller.pdb is PDB) return
        runAsync { client.postSequence(graph.controller.pdb.sequence) }
            .ui { res -> res.map { runAsync { client.waitForBlast(it.requestID) } } }
    }


}

private fun timeConverter(prefix: String): StringConverter<Number> {
    return object : StringConverter<Number>() {
        override fun fromString(string: String?): Number = TODO("not implemented")
        override fun toString(time: Number?): String =
            if (time as Long >= 0) "$prefix: $time seconds"
            else ""
    }
}

private fun idConverter(): StringConverter<String> {
    return object : StringConverter<String>() {
        override fun fromString(string: String?): String = TODO("not implemented")
        override fun toString(id: String?) = if (id.isNullOrEmpty()) id else "ID: $id"
    }
}

private fun statusConverter(): StringConverter<BlastStatus> {
    return object : StringConverter<BlastStatus>() {
        override fun fromString(string: String?): BlastStatus = TODO("not implemented")
        override fun toString(status: BlastStatus?) = "Status: $status"
    }
}