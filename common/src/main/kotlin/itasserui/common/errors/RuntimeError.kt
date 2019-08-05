package itasserui.common.errors

import itasserui.common.serialization.JsonObject

abstract class RuntimeError : JsonObject {
    val errorType get() = javaClass.simpleName
    override fun toString(): String {
        return toJson(2)
    }
}