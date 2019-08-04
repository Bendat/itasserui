package itasserui.common.errors

import arrow.core.Either
import itasserui.common.serialization.JsonObject

typealias Outcome<T> = Either<RuntimeError, T>
typealias Success<T> = Either.Right<T>
typealias Failure<T> = Either.Left<T>

abstract class RuntimeError : JsonObject {
    val errorType get() = javaClass.simpleName
    override fun toString(): String {
        return toJson(2)
    }
}