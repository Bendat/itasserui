@file:Suppress("MemberVisibilityCanBePrivate")

package itasser.app.mytasser.app.installwizard.controller

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.some
import arrow.data.Ior
import arrow.data.NonEmptyList
import arrow.data.nel
import itasser.app.mytasser.kodeinmodules.DependencyInjector
import itasser.app.mytasser.lib.ITasserSettings
import itasserui.app.user.ProfileManager
import itasserui.app.user.UnregisteredUser
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
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.conf.global
import org.kodein.di.generic.instance
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue
import java.nio.file.Path
import java.util.*

@Suppress("unused")
class InstallWizardController(
    override val kodein: Kodein = Kodein.global
) : Controller(), Logger, KodeinAware {
    val id: UUID = uuid

    init {
        info { "Created new controller $id" }
    }

    val database: Database by instance()
    val pm: ProfileManager by instance()

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

    val runStyleProperty = SimpleStringProperty()
    var runStyle: String by runStyleProperty

    val databasePathProperty = SimpleObjectProperty<Path>()
    var databasePath: Path by databasePathProperty

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

    fun initialize(): Option<NonEmptyList<RuntimeError>> {
        DependencyInjector.initializeKodein(name, password, toSettings(), databasePath)
        database.launch()
        val profile = pm.createUserProfile(toUser())
        val dbWrite = database.create(toSettings())
        info { "Creating user profile: $profile" }
        info { "Creating database settings: $dbWrite" }
        return when {
            profile is Ior.Left && dbWrite is Either.Left ->
                NonEmptyList(dbWrite.a, *profile.value.all.toTypedArray()).some()
            profile is Ior.Both && dbWrite is Either.Left ->
                NonEmptyList(dbWrite.a, *profile.leftValue.all.toTypedArray()).some()
            profile is Ior.Left -> profile.value.some()
            profile is Ior.Both -> profile.leftValue.some()
            dbWrite is Either.Left -> dbWrite.a.nel().some()
            else -> None
        }.also {
            initStatus = it
        }.also {
            print("Setting is initialized")
            isInitialized = true
        }

    }
}

