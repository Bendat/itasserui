package itasserui.lib.process.runner

import itasserui.lib.filemanager.FS
import itasserui.lib.process.ArgNames
import itasserui.lib.process.process.CCRCProcess
import itasserui.lib.process.process.ITasser
import java.nio.file.Path
import java.util.*

fun getProcess(file: Path): ITasser {
    val params = listOf("perl", ArgNames.AutoFlush.toString(), file)
        .map(Any::toString)
    return ITasser(
        CCRCProcess(
            id = UUID.randomUUID(),
            seq = file,
            name = "Test Program",
            args = params,
            createdAt = Date(),
            createdBy = UUID.randomUUID(),
            dataDir = FS[""]
        )
    )
}