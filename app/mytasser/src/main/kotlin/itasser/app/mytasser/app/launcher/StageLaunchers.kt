package itasser.app.mytasser.app.launcher

import itasser.app.mytasser.Styles
import itasser.app.mytasser.app.installwizard.InstallWizard
import itasserui.lib.store.Database
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.conf.global
import org.kodein.di.generic.instance
import tornadofx.App
import tornadofx.NoPrimaryViewSpecified
import tornadofx.Stylesheet
import tornadofx.UIComponent
import kotlin.reflect.KClass

abstract class Launcher(
    primaryView: KClass<out UIComponent> = NoPrimaryViewSpecified::class,
    stylesheet: KClass<out Stylesheet>
): App(primaryView, stylesheet), KodeinAware{
    override val kodein: Kodein
        get() = Kodein.global

    val database: Database by instance()
}

class InstallWizardLauncher : Launcher(InstallWizard::class, Styles::class)
