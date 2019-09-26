package itasser.app.mytasser.kodeinmodules

import itasserui.app.user.ProfileManager
import itasserui.lib.filemanager.FS
import itasserui.lib.store.Database
import itasserui.lib.store.Database.MemoryDatabase
import itasserui.lib.store.Database.PersistentDatabase
import org.kodein.di.Kodein
import org.kodein.di.conf.global
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

fun profilemanagermodule() =
    Kodein.Module("Profile Manager Module") {
        bind<ProfileManager>() with singleton {
            ProfileManager()
        }
    }