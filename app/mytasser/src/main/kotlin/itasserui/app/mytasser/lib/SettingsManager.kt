package itasserui.app.mytasser.lib

import com.fasterxml.jackson.annotation.JsonIgnore
import itasserui.common.serialization.DBObject
import itasserui.common.utils.uuid
import javafx.beans.property.SimpleObjectProperty
import tornadofx.getValue
import tornadofx.setValue
import java.nio.file.Path
import java.util.*

class SettingsManager {
    @JsonIgnore
    val itasserProperty: SimpleObjectProperty<ITasserSettings> = SimpleObjectProperty(ITasserSettings())
    var itasser by itasserProperty
}

class ITasserSettings(override val id: UUID = uuid) : DBObject {
    @JsonIgnore
    val pkgdirProperty = SimpleObjectProperty<Path>()
    var pkgDir: Path by pkgdirProperty
    @JsonIgnore
    val libDirProperty = SimpleObjectProperty<Path>()
    var libDir: Path by libDirProperty
    @JsonIgnore
    val javaHomeProperty = SimpleObjectProperty<Path>()
    var javaHome: Path by javaHomeProperty
    @JsonIgnore
    val dataDirProperty = SimpleObjectProperty<Path>()
    var dataDir: Path by dataDirProperty
    @JsonIgnore
    val runStyleProperty = SimpleObjectProperty<String>()
    var runStyle: String by runStyleProperty


    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(pkgDir: Path, libDir: Path, javaHome: Path, dataDir: Path, runStyle: String, id: UUID) : this(id) {
        this.pkgDir = pkgDir
        this.libDir = libDir
        this.javaHome = javaHome
        this.dataDir = dataDir
        this.runStyle = runStyle
    }

    operator fun invoke(op: ITasserSettings.() -> Unit) {
        apply(op)
    }


}
