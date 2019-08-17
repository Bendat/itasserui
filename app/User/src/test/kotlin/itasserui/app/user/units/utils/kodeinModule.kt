package itasserui.app.user.units.utils

import com.apple.eio.FileManager
import itasserui.app.user.User
import itasserui.lib.filemanager.LocalFileManager
import itasserui.lib.store.Database
import itasserui.lib.store.Database.PersistentDatabase
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import java.nio.file.Path

fun testModule(itasserHome: Path, admin: User) = Kodein {
    bind<Database>() with singleton {
        PersistentDatabase(itasserHome, "Test Database", admin.username, admin.password)
    }

    bind<FileManager>() with singleton {
        LocalFileManager with 
    }
}
