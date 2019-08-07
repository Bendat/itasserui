package itasserui.app.user.units.filemanager

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.kotlintest.eventually
import io.kotlintest.seconds
import io.kotlintest.specs.DescribeSpec
import itasserui.lib.filemanager.FileManager
import itasserui.lib.filemanager.LocalFileManager
import java.nio.file.Path

class UserExistsTests : DescribeSpec({
    val jimFS = Jimfs.newFileSystem(Configuration.unix())
    lateinit var testRootPath: Path
    lateinit var fm: FileManager
    eventually(1.seconds) {
        testRootPath = jimFS.getPath("/")
        System.setProperty("itasserui.directory.watch", false.toString())
        fm = LocalFileManager(testRootPath)
    }

    describe("User Exists Tests") {

    }
})