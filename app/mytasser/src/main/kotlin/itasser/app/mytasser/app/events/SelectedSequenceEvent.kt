package itasser.app.mytasser.app.events

import itasserui.lib.process.process.ITasser
import tornadofx.FXEvent

class SelectedSequenceEvent(val sequence: ITasser) : FXEvent()

