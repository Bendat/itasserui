package itasser.app.mytasser.kodeinmodules

import itasser.app.mytasser.lib.ITasserSettings
import itasserui.common.logger.Logger
import itasserui.lib.filemanager.FS
import org.kodein.di.Kodein
import org.kodein.di.conf.global

object DependencyInjector {
    private var wasInitialized = false
    val isInitialized get() = wasInitialized

    fun initializeKodein(
        name: String,
        password: String,
        settings: ITasserSettings
    ) {
        if (!isInitialized) {
            Kodein.global.mutable = true
            Kodein.global.clear()
            val log = object : Logger {}
            log.info { "Initializing the app from Install Wizard" }
            FS.create
                .directories(FS.itasserhome)
                .map { log.info { "Creating itasser home directory: $it" } }
                .map { Kodein.global.addImport(fileManagerModule()) }
            log.trace { "Adding database module with name $name" }
            Kodein.global.addImport(databaseModule(name, password))
            log.trace { "Adding profile manager module" }
            Kodein.global.addImport(profilemanagermodule())
            log.trace { "Adding settings manager module" }
            Kodein.global.addImport(settingsmanagermodule { itasser = settings })
            wasInitialized = true
        }
    }
}