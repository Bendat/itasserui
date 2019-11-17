package itasserui.common.extensions

import arrow.core.Either
import arrow.core.Try
import arrow.data.Ior
import arrow.data.Nel
import itasserui.common.`typealias`.Outcome
import itasserui.common.errors.RuntimeError

typealias Outcomes<T> = Ior<Nel<RuntimeError>, Nel<T>>

operator fun <T> Outcome<T>.plus(
    other: Outcome<T>
): Ior<Nel<RuntimeError>, Nel<T>> =
    when {
        this is Either.Left && other is Either.Left ->
            Ior.Left(Nel.of(this.a, other.a))
        this is Either.Left && other is Either.Right ->
            Ior.Both(Nel.of(this.a), Nel.of(other.b))
        this is Either.Right && other is Either.Left ->
            Ior.Both(Nel.of(other.a), Nel.of(this.b))
        this is Either.Right && other is Either.Right ->
            Ior.Right(Nel.of(this.b, other.b))
        else -> throw IllegalStateException("This should never happen when adding [$this] and [$other]")
    }


fun <T, K> Try<T>.mapLeft(op: (Throwable) -> K) =
    toEither().mapLeft(op)

class ExceptionError(val e: Throwable) : RuntimeError()

fun <T> Try(
    eOp: (Throwable) -> RuntimeError = { ExceptionError(it) },
    op: () -> T
): Outcome<T> {
    return try {
        Either.Right(op())
    } catch (e: Throwable) {
        Either.Left(eOp(e))
    }
}