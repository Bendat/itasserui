package itasserui.app.itasserui.installwizard.controller

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User
import itasserui.app.user.UnregisteredUser
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.logger.Logger
import itasserui.common.serialization.DBObject
import itasserui.common.utils.uuid
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

data class Settings(
    val pkgDir: Path,
    val libDir: Path,
    val javaHome: Path,
    val dataDIr: Path,
    val runStyle: String,
    override val id: UUID = uuid
) : DBObject

class InstallWizardController : Logger {
    val nameProperty = SimpleStringProperty()
    var name by nameProperty

    val emailProperty = SimpleStringProperty()
    var email by emailProperty

    val passwordProperty = SimpleStringProperty()
    var password by passwordProperty

    val repeatPasswordProperty = SimpleStringProperty()
    var repeatPassword by repeatPasswordProperty

    val pkgDirProperty = SimpleStringProperty()
    var pkgDir by pkgDirProperty

    val libDirProperty = SimpleStringProperty()
    var libDir by libDirProperty

    val javaHomeProperty = SimpleStringProperty()
    var javaHome by javaHomeProperty

    val dataDirProperty = SimpleStringProperty()
    var dataDir by dataDirProperty

    val runStyleProperty = SimpleStringProperty()
    var runStyle by runStyleProperty

    fun toSettings(): Settings {
        info { "datadir is [$dataDir]" }
        return Settings(
            pkgDir = Paths.get(pkgDir),
            libDir = Paths.get(libDir),
            javaHome = Paths.get(javaHome),
            runStyle = runStyle,
            dataDIr = Paths.get(dataDir)
        )
    }

    fun toUser(): UnregisteredUser {
        return UnregisteredUser(
            Username(name),
            RawPassword(password),
            EmailAddress(email)
        )
    }
}