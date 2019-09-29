package itasser.app.mytasser.app.installwizard.viewmodel

import arrow.core.Option
import itasser.app.mytasser.app.installwizard.controller.InstallWizardController
import itasserui.common.utils.uuid
import tornadofx.ItemViewModel

private typealias SettingsUserResult = Pair<Option<Long>, Option<Long>>

class InstallWizardViewModel() :
    ItemViewModel<InstallWizardController>(InstallWizardController()) {
    val id = uuid
    val name = bind(InstallWizardController::nameProperty, autocommit = true)
    val email = bind(InstallWizardController::emailProperty, autocommit = true)
    val password = bind(InstallWizardController::passwordProperty, autocommit = true)
    val passwordRepeat = bind(InstallWizardController::repeatPasswordProperty, autocommit = true)

    val pkgDir = bind(InstallWizardController::pkgDirProperty, autocommit = true)
    val libDir = bind(InstallWizardController::libDirProperty, autocommit = true)
    val javaHome = bind(InstallWizardController::javaHomeProperty, autocommit = true)
    val dataDir = bind(InstallWizardController::dataDirProperty, autocommit = true)
    val runStyle = bind(InstallWizardController::runStyleProperty, autocommit = true)

    var databasePath = bind(InstallWizardController::databasePathProperty)
}