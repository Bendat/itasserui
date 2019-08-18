package itasser.app.mytasser.app.installwizard.viewmodel

import arrow.core.Option
import itasser.app.mytasser.app.installwizard.controller.InstallWizardController
import itasserui.common.extensions.Outcomes
import itasserui.common.extensions.plus
import itasserui.lib.store.Database
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import tornadofx.ItemViewModel

private typealias SettingsUserResult = Pair<Option<Long>, Option<Long>>

class InstallWizardViewModel :
    ItemViewModel<InstallWizardController>(InstallWizardController()),
    KodeinAware {
    override val kodein: Kodein get() = item.kodein
//    val db by instance<Database>()
    val name = bind(InstallWizardController::nameProperty, autocommit = true)
    val email = bind(InstallWizardController::emailProperty, autocommit = true)
    val password = bind(InstallWizardController::passwordProperty, autocommit = true)
    val passwordRepeat = bind(InstallWizardController::repeatPasswordProperty, autocommit = true)

    val pkgDir = bind(InstallWizardController::pkgDirProperty, autocommit = true)
    val libDir = bind(InstallWizardController::libDirProperty, autocommit = true)
    val javaHome = bind(InstallWizardController::javaHomeProperty, autocommit = true)
    val dataDir = bind(InstallWizardController::dataDirProperty, autocommit = true)
    val runStyle = bind(InstallWizardController::runStyleProperty, autocommit = true)

    fun save(): Outcomes<Long> =
        item.database.create(toSettings()) +
                item.database.create(toUser().toUser())

    fun toUser() = item.toUser()
    fun toSettings() = item.toSettings()
}