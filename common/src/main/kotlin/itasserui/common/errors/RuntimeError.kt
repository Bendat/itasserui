package itasserui.common.errors

import arrow.core.Failure
import arrow.core.Success
import itasserui.common.errors.MonadError.EmptyOptionError
import itasserui.common.serialization.JsonObject
import itasserui.common.serialization.Serializer

abstract class RuntimeError(val parentError: RuntimeError? = null) : JsonObject {
    val errorType get() = javaClass.simpleName.removeSuffix("Error")
}
typealias EmptyOption = EmptyOptionError

class ExceptionError(val e: Throwable, parent: RuntimeError? = null) : RuntimeError(parent)

sealed class MonadError(parent: RuntimeError?) : RuntimeError(parent) {
    class EmptyOptionError(parent: RuntimeError?) : MonadError(parent)
}