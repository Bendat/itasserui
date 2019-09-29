package itasser.app.mytasser.kodeinmodules

import itasserui.app.user.ProfileManager
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

fun profilemanagermodule() =
    Kodein.Module("Profile Manager Module") {
        bind<ProfileManager>() with singleton {
            ProfileManager()
        }
    }