package itasserui.app.user.units.utils

import io.kotlintest.shouldBe
import itasserui.lib.filemanager.FileManager
import itasserui.lib.filemanager.LocalFileManager
import itasserui.lib.store.Database
import itasserui.lib.store.Database.PersistentDatabase
import itasserui.test_utils.matchers.Be.none
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import java.nio.file.Path

fun testModule(itasserHome: Path, username: String, pass: String) =
    Kodein.Module("Test Module") {
        bind<Database>() with singleton {
            PersistentDatabase(itasserHome, "Test Database", username, pass)
                .also { it.launch() shouldBe none() }
        }

        bind<FileManager>() with singleton {
            LocalFileManager(itasserHome)
        }
    }
