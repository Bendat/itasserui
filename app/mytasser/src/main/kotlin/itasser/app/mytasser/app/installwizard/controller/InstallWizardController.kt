@file:Suppress("MemberVisibilityCanBePrivate")

package itasser.app.mytasser.app.installwizard.controller

import arrow.core.*
import arrow.data.Ior
import arrow.data.NonEmptyList
import arrow.data.nel
import itasser.app.mytasser.kodeinmodules.DependencyInjector.initializeKodein
import itasser.app.mytasser.lib.ITasserSettings
import itasserui.app.user.ProfileManager
import itasserui.app.user.UnregisteredUser
import itasserui.common.`typealias`.NelOutcome
import itasserui.common.errors.RuntimeError
import itasserui.common.interfaces.inline.EmailAddress
import itasserui.common.interfaces.inline.RawPassword
import itasserui.common.interfaces.inline.Username
import itasserui.common.logger.Logger
import itasserui.common.utils.uuid
import itasserui.lib.filemanager.FS
import itasserui.lib.store.Database
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue
import java.nio.file.Path
import java.util.*

@Suppress("unused")
class InstallWizardController : Controller(), Logger {
    val id: UUID = uuid

    init {
        info { "Created new controller $id" }
    }

    val nameProperty = SimpleStringProperty()
    var name: String by nameProperty

    val emailProperty = SimpleStringProperty()
    var email: String by emailProperty

    val passwordProperty = SimpleStringProperty()
    var password: String by passwordProperty

    val repeatPasswordProperty = SimpleStringProperty()
    var repeatPassword: String by repeatPasswordProperty

    val pkgDirProperty = SimpleStringProperty()
    var pkgDir: String by pkgDirProperty

    val libDirProperty = SimpleStringProperty()
    var libDir: String by libDirProperty

    val javaHomeProperty = SimpleStringProperty()
    var javaHome: String by javaHomeProperty

    val dataDirProperty = SimpleStringProperty()
    var dataDir: String by dataDirProperty

    val runStyleProperty = SimpleStringProperty("gnuparallel")
    var runStyle: String by runStyleProperty

    val newRunStyleProperty = SimpleObjectProperty<ToggleGroup>()
    val newRunStyle by newRunStyleProperty

    val databasePathProperty = SimpleObjectProperty<Path>()
    var databasePath: Path by databasePathProperty

    val latestKodeinProperty = SimpleObjectProperty<Option<Kodein>>(None)
    var latestKodein: Option<Kodein> by latestKodeinProperty

    @Volatile
    var isInitialized = false
    internal var initStatus: Option<NonEmptyList<RuntimeError>> = None

    private fun toSettings(): ITasserSettings {
        info { "datadir is [$dataDir]" }
        return ITasserSettings().apply {
            pkgDir = FS[this@InstallWizardController.pkgDir]
            libDir = FS[this@InstallWizardController.libDir]
            javaHome = FS[this@InstallWizardController.javaHome]
            runStyle = this@InstallWizardController.runStyle
            dataDir = FS[this@InstallWizardController.dataDir]
        }
    }

    private fun toUser(): UnregisteredUser {
        return UnregisteredUser(
            Username(name),
            RawPassword(password),
            EmailAddress(email)
        )
    }

    fun initialize(): NelOutcome<Kodein> {
        val di = initializeKodein(name, password, toSettings(), databasePath)
        latestKodein = Some(di)
        val db by di.instance<Database>()
        val pm by di.instance<ProfileManager>()
        db.launch()
        val profile = pm.createUserProfile(toUser())
        val dbWrite = db.create(toSettings())
        info { "Creating user profile: $profile" }
        info { "Creating database settings: $dbWrite" }
        return when {
            profile is Ior.Left && dbWrite is Either.Left ->
                NonEmptyList(dbWrite.a, *profile.value.all.toTypedArray()).left()
            profile is Ior.Both && dbWrite is Either.Left ->
                NonEmptyList(dbWrite.a, *profile.leftValue.all.toTypedArray()).left()
            profile is Ior.Left -> profile.value.left()
            profile is Ior.Both -> profile.leftValue.left()
            dbWrite is Either.Left -> dbWrite.a.nel().left()
            else -> di.right()
        }
    }
}

