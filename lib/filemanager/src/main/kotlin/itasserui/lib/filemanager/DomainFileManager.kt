package itasserui.lib.filemanager

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class DomainFileManager(val basedir: Path) {
    val domains = mutableMapOf<FileDomain, DomainDirectory>()

    fun new(domain: FileDomain): DomainDirectory {
        val directory = DomainDirectory(basedir, domain)
        domains[domain] = directory
        return directory
    }

    fun exists(domain: FileDomain) =
        domains.any { it.key == domain } || Files.exists(basedir.resolve(domain.relativeRoot))

    fun fullPath(domain: FileDomain) =
        domains[domain]?.unixPath ?: Paths.get("/No-file-found-for-domain$domain")

    operator fun get(domain: FileDomain) = domains[domain]
}