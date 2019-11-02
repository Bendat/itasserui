package itasser.app.mytasser.kodeinmodules

import itasserui.app.mytasser.lib.ITasserSettings
import itasserui.common.logger.Logger
import itasserui.lib.filemanager.FS
import org.kodein.di.Kodein
import org.kodein.di.conf.ConfigurableKodein
import java.nio.file.Path

object DependencyInjector {
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