package itasserui.app.itasserui.installwizard.viewmodel

import arrow.core.*
import itasserui.app.itasserui.installwizard.controller.InstallWizardController
import itasserui.common.`typealias`.Outcome
import itasserui.common.errors.EmptyOption
import itasserui.lib.store.Database
import tornadofx.ItemViewModel

private typealias SettingsUserResult = Pair<Option<Long>, Option<Long>>

class InstallWizardViewModel :
    ItemViewModel<InstallWizardController>(InstallWizardController()) {
    val db by di<Database>()
    val name = bind(InstallWizardController::nameProperty, autocommit = true)
    val email = bind(InstallWizardController::emailProperty, autocommit = true)
    val password = bind(InstallWizardController::passwordProperty, autocommit = true)
    val passwordRepeat = bind(InstallWizardController::repeatPasswordProperty, autocommit = true)

    val pkgDir = bind(InstallWizardController::pkgDirProperty, autocommit = true)
    val libDir = bind(InstallWizardController::libDirProperty, autocommit = true)
    val javaHome = bind(InstallWizardController::javaHomeProperty, autocommit = true)
    val dataDir = bind(InstallWizardController::dataDirProperty, autocommit = true)
    val runStyle = bind(InstallWizardController::runStyleProperty, autocommit = true)

    fun save(db: Option<Database>): Outcome<SettingsUserResult> {
        return when (db) {
            is None -> Left(EmptyOption("Database object is of type [$None]"))
            is Some -> Right(db.t.create(toSettings()) to db.t.create(toUser()))
        }
    }

    fun toUser() = item.toUser()
    fun toSettings() = item.toSettings()
}