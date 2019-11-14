package itasserui.lib.filemanager

import java.nio.file.Path

class DomainFileManager(val basedir: Path) {
    val domains = mutableMapOf<FileDomain, DomainDirectory>()

    fun new(domain: FileDomain): DomainDirectory {
        val directory = DomainDirectory(basedir, domain)
        domains[domain] = directory
        return directory
    }

    operator fun get(domain: FileDomain) = domains[domain]
}