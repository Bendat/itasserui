package itasser.app.mytasser.app.installwizard

import itasser.app.mytasser.app.installwizard.viewmodel.InstallWizardViewModel
import itasser.app.mytasser.app.installwizard.views.ITasserSetupPage
import itasser.app.mytasser.app.installwizard.views.InstallWizardPage
import itasser.app.mytasser.app.installwizard.views.RegistrationPage
import itasser.app.mytasser.lib.SettingsManager
import itasserui.common.logger.Logger
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.conf.global
import org.kodein.di.generic.instance
import tornadofx.Wizard

class InstallWizard : Wizard(), KodeinAware, Logger {
    override val kodein: Kodein
        get() = Kodein.global
    override val canFinish = allPagesComplete
    override val canGoNext = currentPageComplete

    val model: InstallWizardViewModel by inject()
    val controller by lazy { model.item }
    val settings: SettingsManager by instance()

    init {
        info { "Model has id ${model.item.id}" }
        add(RegistrationPage::class)
        add(ITasserSetupPage::class)
        this.pages
            .map { it as InstallWizardPage }
            .forEach { info { "Page ${it::class.simpleName} has controller ${it.model.item.id}" } }
    }

    override fun onSave() {
        info { "Calling oncomplete" }
        model.item.initialize()
        info { "Completed installation" }
        super.onSave()
    }
}