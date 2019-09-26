package itasser.app.mytasser.app.installwizard.controller

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.some
import arrow.data.Ior
import arrow.data.NonEmptyList
import arrow.data.nel
import itasser.app.mytasser.kodeinmodules.*
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
import javafx.beans.property.SimpleStringProperty
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.conf.global
import org.kodein.di.generic.instance
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue

class InstallWizardController(
    override val kodein: Kodein = Kodein.global
) : Controller(), Logger, KodeinAware {
    val id = uuid

    init {
        info { "Created new controller $id" }
    }

    val database: Database by instance()
    val pm: ProfileManager by instance()

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
//        Kodein.global.mutable = true
        diInitializer(name, password, toSettings())
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

