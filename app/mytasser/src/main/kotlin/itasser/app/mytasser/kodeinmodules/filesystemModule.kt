package itasser.app.mytasser.kodeinmodules

import itasserui.lib.filemanager.DomainFileManager
import itasserui.lib.filemanager.FS
import org.kodein.di.Kodein.Module
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

fun fileManagerModule(): Module = Module("Local File Manager Module") {
    bind<DomainFileManager>() with singleton { DomainFileManager(FS.itasserhome) }
}