package itasserui.common.errors

import arrow.core.Failure
import arrow.core.Success
import itasserui.common.serialization.JsonObject
import itasserui.common.serialization.Serializer

abstract class RuntimeError(val parentError: RuntimeError? = null) : JsonObject {
    val errorType get() = javaClass.simpleName
    override fun toString(): String {
        return when (val res = Serializer.tryToJson(this)) {
            is Failure -> "Could not serialize ${this::class.simpleName}[$res]"
            is Success -> res.value
        }
    }
}