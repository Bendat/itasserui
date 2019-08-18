package itasser.app.mytasser.kodeinmodules

import itasserui.lib.store.Database
import itasserui.lib.store.Database.PersistentDatabase
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import java.nio.file.Path

fun databaseModule(itasserhome: Path, username: String, password: String) =
    Kodein.Module("Database Module") {
        bind<Database>() with singleton {
            PersistentDatabase(itasserhome, "database", username, password)
        }
    }