package itasser.app.mytasser.kodeinmodules

import itasser.app.mytasser.lib.SettingsManager
import itasserui.app.user.ProfileManager
import itasserui.lib.filemanager.FS
import itasserui.lib.filemanager.FileManager
import itasserui.lib.filemanager.LocalFileManager
import itasserui.lib.process.manager.ProcessManager
import itasserui.lib.store.Database
import itasserui.lib.store.Database.MemoryDatabase
import itasserui.lib.store.Database.PersistentDatabase
import org.kodein.di.Kodein.Module
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import java.nio.file.Path

fun kodeinModules(
    dbUsername: String,
    dbPassword: String,
    homeDirectory: Path = FS.itasserhome,
    settings: SettingsManager.() -> Unit
) = Module("Primary module") {
    val md = MemoryDatabase(dbUsername, dbPassword)
    val db = PersistentDatabase(homeDirectory, "database", dbUsername, dbPassword)
    val fm = LocalFileManager(homeDirectory)
    val pm = ProfileManager(fm, db)
    val sm = SettingsManager().apply(settings)
    val im = ProcessManager()
    println("Creating File Manager module")
    bind<FileManager>() with singleton { fm }
    println("Creating Database module")
    bind<Database>() with singleton { db }
    bind<Database>("memdb") with singleton { md }
    println("Creating Profile Manager module")
    bind<ProfileManager>() with singleton { pm }
    println("Creating Settings Manager module")
    bind<SettingsManager>() with singleton { sm }
    println("Binding Process manager module")
    bind<ProcessManager>() with singleton { im }

}