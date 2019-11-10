package itasser.app.mytasser.app.process.newDialog

import itasserui.app.mytasser.lib.extensions.bind
import itasserui.app.mytasser.lib.kInject
import itasserui.app.user.ProfileManager
import itasserui.lib.process.Arg
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import tornadofx.Controller
import java.nio.file.Path

class NewProcessController : Controller() {
    val profileManager: ProfileManager by kInject<ProfileManager>()
    val usersProperty = FXCollections.observableArrayList<String>()
        .bind(profileManager.profiles) { it.user.username.value }
    val user = SimpleObjectProperty("")
    val seqNameProperty = SimpleObjectProperty<String>(null)
    val dataDirProperty = SimpleObjectProperty<Path?>(null)
    val outDirProperty = SimpleObjectProperty<Path?>(null)
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
}
