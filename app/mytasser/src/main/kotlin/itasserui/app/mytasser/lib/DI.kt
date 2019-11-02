package itasserui.app.mytasser.lib

import itasserui.common.logger.Logger
import org.kodein.di.Instance
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.conf.global
import org.kodein.di.generic
import tornadofx.Component
import tornadofx.ScopedInstance
import kotlin.reflect.KProperty

class DI(
    override val kodein: Kodein = Kodein.global
) : Component(), KodeinAware, ScopedInstance


inline fun <reified T : Any> Component.kInject(): KodeinInjector {
    return KodeinInjector { find<DI>() }
}

class KodeinInjector(val op: () -> DI) : Logger {
    inline operator fun <reified T : Any> getValue(thisRef: Any, property: KProperty<*>): T {
        try {
            val di = op()
            val res by di.Instance<T>(generic())
            return res
        } catch (e: Throwable) {
            error { "Dependency inject for ${T::class.qualifiedName} failed. Is the DI module in component scope?" }
            throw e
        }
    }
}