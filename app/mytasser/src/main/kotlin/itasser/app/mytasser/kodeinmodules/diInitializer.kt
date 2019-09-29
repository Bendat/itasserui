package itasser.app.mytasser.kodeinmodules

import itasser.app.mytasser.lib.ITasserSettings
import itasserui.common.logger.Logger
import itasserui.lib.filemanager.FS
import itasserui.lib.store.Database
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.conf.ConfigurableKodein
import org.kodein.di.conf.global
import org.kodein.di.generic.instance
import java.nio.file.Path

object DependencyInjector : KodeinAware {
    override val kodein: Kodein = Kodein.global

    private var wasInitialized = false
    val isInitialized get() = wasInitialized

    private val database: Database by instance()

    fun initializeKodein(
        name: String,
        password: String,
        settings: ITasserSettings,
        homdeDir: Path = FS.itasserhome
    ): Kodein {
        val kodein = ConfigurableKodein(mutable = true)
        val log = object : Logger {}
        log.info { "Initializing the app from Install Wizard" }
        FS.create.directories(FS.itasserhome)
        log.trace { "Adding the kodein module" }
        kodein.addImport(kodeinModules(
            name,
            password,
            homdeDir
        ) { itasser = settings })
        return kodein
    }
}