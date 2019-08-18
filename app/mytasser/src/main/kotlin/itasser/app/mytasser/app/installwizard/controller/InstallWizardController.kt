package itasser.app.mytasser.app.installwizard.controller

import itasser.app.mytasser.kodeinmodules.databaseModule
import itasser.app.mytasser.kodeinmodules.fileManagerModule
import itasserui.app.user.UnregisteredUser
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.logger.Logger
import itasserui.common.serialization.DBObject
import itasserui.common.utils.uuid
import itasserui.lib.filemanager.FS
import itasserui.lib.store.Database
import javafx.beans.property.SimpleStringProperty
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.conf.global
import org.kodein.di.generic.instance
import tornadofx.getValue
import tornadofx.setValue
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

class InstallWizardController(
    override val kodein: Kodein = Kodein.global
) : Logger, KodeinAware {
    val database: Database by instance()
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

    fun initialize() {
        FS.create.directories(FS.itasserhome)
            .map { Kodein.global.addImport(fileManagerModule(it)) }
        Kodein.global.addImport(databaseModule(FS.itasserhome, name, password))
    }
}