package itasser.app.mytasser.kodeinmodules

import itasserui.lib.filemanager.FS
import itasserui.lib.filemanager.FileManager
import itasserui.lib.filemanager.LocalFileManager
import org.kodein.di.Kodein.Module
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import java.nio.file.Path

fun fileManagerModule(): Module = Module("Local File Manager Module") {
    bind<FileManager>() with singleton { LocalFileManager(FS.itasserhome) }
}