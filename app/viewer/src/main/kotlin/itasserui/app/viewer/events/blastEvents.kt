package itasserui.app.viewer.events

import itasserui.app.viewer.blast.BlastStatus
import itasserui.lib.pdb.parser.PDB
import tornadofx.FXEvent

/***************************************
 * Blast Singleton Events               *
 ***************************************/
object BlastEndedEvent : FXEvent()

object BlastStartedEvent : FXEvent()
object BlastStartingEvent: FXEvent()
object BlastAlreadyRunningEvent : FXEvent()

/***************************************
 * Blast Data Events                   *
 ***************************************/
data class BlastStatusEvent(val status: BlastStatus) : FXEvent()
data class BlastRemoteAlignmentsEvent(val text: String): FXEvent()

/***************************************
 * PDB Data Events                   *
 ***************************************/
data class PDBLoadedEvent(val pdb: PDB) : FXEvent()
