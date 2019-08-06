package itasserui.common.`typealias`

import arrow.core.Either
import itasserui.common.errors.RuntimeError

typealias Result<TOK> = Either<RuntimeError, TOK>
typealias Outcome<T> = Either<RuntimeError, T>

typealias OK<TOK> = Either.Right<TOK>
typealias Err = Either.Left<RuntimeError>