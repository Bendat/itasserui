package itasserui.lib.process.process

import java.nio.file.Path
import java.util.*

data class CCRCProcess(
    val id: UUID,
    val name: String,
    val args: List<String>,
    val createdAt: Date,
    val createdBy: UUID,
    val seq: Path,
    val dataDir: Path
)