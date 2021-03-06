package itasser.app.mytasser.app.process.newDialog

import itasser.app.mytasser.lib.extensions.bind
import itasser.app.mytasser.lib.kInject
import itasserui.app.user.ProfileManager
import itasserui.app.user.ProfileManager.Profile
import itasserui.lib.process.Arg
import itasserui.lib.process.manager.ProcessManager
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.onChange
import tornadofx.setValue
import java.nio.file.Path

class NewProteinDialogController : Controller() {
    val profileManager: ProfileManager by kInject()
    val processManager: ProcessManager by kInject()
    val usersProperty = FXCollections.observableArrayList<String>()
        .bind(profileManager.profiles) { it.user.username.value }
    val userProperty = SimpleObjectProperty("")
    var user: String by userProperty
    val profileProperty = SimpleObjectProperty<Profile?>(null)
    var profile: Profile? by profileProperty
    val nameProperty = SimpleObjectProperty("")
    val name: String? by nameProperty
    val seqNameProperty = SimpleObjectProperty("")
    val seqName: String by seqNameProperty
    val seqFileProperty = SimpleObjectProperty<Path?>(null)
    val seqFile by seqFileProperty
    val dataDirProperty = SimpleObjectProperty<Path?>(null)
    var dataDir by dataDirProperty
    val outDirProperty = SimpleObjectProperty<Path?>(null)
    val outDir by outDirProperty
    val homoFlagProperty = SimpleObjectProperty(Arg.HomoFlag.argType.default)
    val idCutProperty = SimpleObjectProperty(Arg.IdCut.argType.default)
    val nTempProperty = SimpleObjectProperty(Arg.NTemp.argType.default)
    val nModelProperty = SimpleObjectProperty(Arg.NModel.argType.default)
    val ecProperty = SimpleObjectProperty(Arg.EC.argType.default)
    val lbsProperty = SimpleObjectProperty(Arg.LBS.argType.default)
    val goProperty = SimpleObjectProperty(Arg.GO.argType.default)
    val tempExclProperty = SimpleObjectProperty<Path?>(null)
    val restraint1Property = SimpleObjectProperty<Path?>(null)
    val restraint2Property = SimpleObjectProperty<Path?>(null)
    val restraint3Property = SimpleObjectProperty<Path?>(null)
    val restraint4Property = SimpleObjectProperty<Path?>(null)
    val trajProperty = SimpleObjectProperty<Boolean>(null)
    val lightProperty = SimpleObjectProperty(false)
    val hoursProperty = SimpleObjectProperty<Int?>(null)

    init {
        nameProperty.onChange { println("Changed") }
    }
}
