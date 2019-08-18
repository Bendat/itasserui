package itasser.app.mytasser.app.installwizard

import itasser.app.mytasser.app.installwizard.controller.InstallWizardController
import itasser.app.mytasser.app.installwizard.viewmodel.InstallWizardViewModel
import itasser.app.mytasser.app.installwizard.views.ITasserSetupPage
import itasser.app.mytasser.app.installwizard.views.RegistrationPage
import tornadofx.Wizard

class InstallWizard : Wizard() {
    override val canFinish = allPagesComplete
    override val canGoNext = currentPageComplete

    private val controller = InstallWizardController()
    val model by inject<InstallWizardViewModel>(params = mapOf("controller" to controller))

    init {
        add(RegistrationPage::class)
        add(ITasserSetupPage::class)
    }
}