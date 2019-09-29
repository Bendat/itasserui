package itasser.app.mytasser.kodeinmodules

import itasserui.lib.store.Database
import itasserui.lib.store.Database.MemoryDatabase
import itasserui.lib.store.Database.PersistentDatabase
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import java.nio.file.Path

fun databaseModule(username: String, password: String, path: Path) =
    Kodein.Module("Database Module") {
        bind<Database>() with singleton {
            PersistentDatabase(path, "database", username, password)
        }
        bind<Database>("memdb") with singleton {
            MemoryDatabase(username, password)
        }

    }