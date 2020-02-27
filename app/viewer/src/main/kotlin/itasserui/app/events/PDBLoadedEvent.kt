package itasserui.app.events

import itasserui.lib.pdb.parser.PDB
import tornadofx.FXEvent

data class PDBLoadedEvent(val pdb: PDB) : FXEvent()
