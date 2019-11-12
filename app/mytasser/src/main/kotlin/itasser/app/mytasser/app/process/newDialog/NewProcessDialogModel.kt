package itasser.app.mytasser.app.process.newDialog

import itasserui.app.mytasser.lib.SettingsManager
import itasserui.app.mytasser.lib.kInject
import itasserui.common.utils.uuid
import itasserui.lib.process.Arg
import itasserui.lib.process.manager.ProcessManager
import itasserui.lib.process.process.ITasser
import tornadofx.ItemViewModel
import java.nio.file.Paths

class NewProcessDialogModel : ItemViewModel<NewProcessController>(NewProcessController()) {
    val profileManager = bind(NewProcessController::profileManager)
    val processManager: ProcessManager by kInject()
    val settings: SettingsManager by kInject()
    val user = bind(NewProcessController::userProperty)
    val seqFile = bind(NewProcessController::seqFileProperty)
    val name = bind(NewProcessController::nameProperty)
    val profile = bind(NewProcessController::profileProperty)
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

    fun moveFasta(){

    }
    fun makeProcess(): ITasser {
        val optionalArgs = mapOf<String, String?>(
            Arg.HomoFlag.arg to homoFlag.value,
            Arg.TempExcl.arg to tempExcl.value.value.toString(),
            Arg.Restraint1.arg to restraint1.value.value.toString(),
            Arg.Restraint2.arg to restraint2.value.value.toString(),
            Arg.Restraint3.arg to restraint3.value.value.toString(),
            Arg.Restraint4.arg to restraint4.value.value.toString(),
            Arg.Hours.arg to hours.value.value.toString()
        ).filter { it.value == null }

        val args = arrayListOf<String>(
            Arg.SeqName.arg, seqName.value,
            Arg.DataDir.arg, dataDir.value.value.toString(),
            Arg.OutDir.arg, outDir.value.value.toString(),
            Arg.LibDir.arg, settings.itasser.libDir.toString(),
            Arg.JavaHome.arg, settings.itasser.javaHome.toString(),
            Arg.PkgDir.arg, settings.itasser.pkgDir.toString(),
            Arg.RunStyle.arg, settings.itasser.runStyle,
            Arg.IdCut.arg, idCut.value.toString(),
            Arg.NTemp.arg, nTemp.value.toString(),
            Arg.NModel.arg, nModel.value.toString(),
            Arg.EC.arg, ec.value.toString(),
            Arg.LBS.arg, lbs.value.toString(),
            Arg.GO.arg, go.value.toString(),
            Arg.Light.arg, light.value.toString(),
            Arg.Traj.arg, traj.value.toString()
        )

        optionalArgs.forEach { (key, value) ->
            args.add(key)
            args.add(value!!)
        }

        return processManager.new(
            uuid, 0, seqFile.value.value ?: Paths.get("/seqfile empty"),
            name.value, args, profile.value.value!!.user.id, dataDir.value.value!!,
            outDir.value.value!!
        )
    }
}