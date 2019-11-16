package itasser.app.mytasser.app.process.newDialog

import itasser.app.mytasser.lib.SettingsManager
import itasser.app.mytasser.lib.kInject
import itasserui.common.logger.Logger
import itasserui.common.utils.uuid
import itasserui.lib.process.Arg
import itasserui.lib.process.process.ITasser
import tornadofx.ItemViewModel
import java.nio.file.Files
import java.nio.file.Paths

class NewProteinDialogModel :
    ItemViewModel<NewProteinDialogController>(NewProteinDialogController()),
    Logger {
    val settings: SettingsManager by kInject()
    val user = bind(NewProteinDialogController::userProperty)
    val seqFile = bind(NewProteinDialogController::seqFileProperty)
    val name = bind(NewProteinDialogController::nameProperty)
    val profile = bind(NewProteinDialogController::profileProperty)
    val seqName = bind(NewProteinDialogController::seqNameProperty)
    val users = bind(NewProteinDialogController::usersProperty)
    val dataDir = bind(NewProteinDialogController::dataDirProperty)
    val outDir = bind(NewProteinDialogController::outDirProperty)
    val homoFlag = bind(NewProteinDialogController::homoFlagProperty)
    val idCut = bind(NewProteinDialogController::idCutProperty)
    val nTemp = bind(NewProteinDialogController::nTempProperty)
    val nModel = bind(NewProteinDialogController::nModelProperty)
    val ec = bind(NewProteinDialogController::ecProperty)
    val lbs = bind(NewProteinDialogController::lbsProperty)
    val go = bind(NewProteinDialogController::goProperty)
    val tempExcl = bind(NewProteinDialogController::tempExclProperty)
    val restraint1 = bind(NewProteinDialogController::restraint1Property)
    val restraint2 = bind(NewProteinDialogController::restraint2Property)
    val restraint3 = bind(NewProteinDialogController::restraint3Property)
    val restraint4 = bind(NewProteinDialogController::restraint4Property)
    val traj = bind(NewProteinDialogController::trajProperty)
    val light = bind(NewProteinDialogController::lightProperty)
    val hours = bind(NewProteinDialogController::hoursProperty)

    fun moveFasta() {
        item.seqFile?.let { seqfile ->
            item.dataDir?.let { dataDir ->
                Files.createDirectories(dataDir)
                Files.copy(seqfile, dataDir)
            }

            item.outDir?.let { outDir ->
                Files.createDirectories(outDir)
            }
        }
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
        info { "Profile is ${item.profile}" }
        info { "seqfile is ${seqFile.value.value}" }
        info { "datadir is ${dataDir.value.value}" }
        info { "Outdir is ${outDir.value.value}" }
        info { "Seqname is ${seqName.value}" }
        return item.processManager.new(
            uuid, 0, seqFile.value.value ?: Paths.get("/seqfile empty"),
            seqName.value, args, item.profile!!.user.id, dataDir.value.value!!,
            outDir.value.value!!
        )
    }
}