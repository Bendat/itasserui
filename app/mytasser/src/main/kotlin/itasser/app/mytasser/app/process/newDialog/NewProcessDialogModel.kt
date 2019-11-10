package itasser.app.mytasser.app.process.newDialog

import tornadofx.ItemViewModel

class NewProcessDialogModel : ItemViewModel<NewProcessController>(NewProcessController()) {
    val profileManager = bind(NewProcessController::profileManager)
    val user = bind(NewProcessController::user)
    val seqName = bind(NewProcessController::seqNameProperty)
    val users = bind(NewProcessController::usersProperty)
    val dataDir = bind(NewProcessController::dataDirProperty)
    val outDir = bind(NewProcessController::outDirProperty)
    val homoFlag = bind(NewProcessController::homoFlagProperty)
    val idCut = bind(NewProcessController::idCutProperty)
    val nTemp = bind(NewProcessController::nTempProperty)
    val nModel = bind(NewProcessController::nModelProperty)
    val ec = bind(NewProcessController::ecProperty)
    val lbs = bind(NewProcessController::lbsProperty)
    val go = bind(NewProcessController::goProperty)
    val tempExcl = bind(NewProcessController::tempExclProperty)
    val restraint1 = bind(NewProcessController::restraint1Property)
    val restraint2 = bind(NewProcessController::restraint2Property)
    val restraint3 = bind(NewProcessController::restraint3Property)
    val restraint4 = bind(NewProcessController::restraint4Property)
    val traj = bind(NewProcessController::trajProperty)
    val light = bind(NewProcessController::lightProperty)
    val hours = bind(NewProcessController::hoursProperty)
}