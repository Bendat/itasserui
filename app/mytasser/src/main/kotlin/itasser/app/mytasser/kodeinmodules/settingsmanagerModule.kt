package itasser.app.mytasser.kodeinmodules

import itasser.app.mytasser.lib.SettingsManager
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

fun settingsmanagermodule(op: SettingsManager.()->Unit) =
    Kodein.Module("Settings Manager Module") {
        bind<SettingsManager>() with singleton {
            SettingsManager().apply(op)
        }
    }
