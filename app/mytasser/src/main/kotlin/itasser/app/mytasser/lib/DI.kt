package itasser.app.mytasser.lib

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.conf.global
import tornadofx.Component
import tornadofx.Scope
import tornadofx.ScopedInstance

class DI(
    override val scope: Scope,
    override val kodein: Kodein = Kodein.global
) :  Component(), KodeinAware, ScopedInstance
