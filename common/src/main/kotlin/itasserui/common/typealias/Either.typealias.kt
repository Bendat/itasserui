package itasserui.common.`typealias`

import arrow.core.Either
import itasserui.common.errors.RuntimeError

typealias Result<TOK> = Either<RuntimeError, TOK>

typealias OK<TOK> = Either.Right<TOK>
typealias Err<TErr> = Either.Left<TErr>