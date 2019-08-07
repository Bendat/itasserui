package itasserui.test_utils.matchers

import arrow.core.*
import io.kotlintest.Matcher
import io.kotlintest.Result
import org.skyscreamer.jsonassert.JSONAssert

val be = Be

object Be {
    fun json(strict: Boolean = false, other: () -> String) = object : Matcher<String> {
        override fun test(value: String): Result {
            val check = Try { JSONAssert.assertEquals(value, other(), strict) }
            val message = when (check) {
                is Success -> "Json [$value] should equal [$other()]"
                is Failure -> check.exception.message as String
            }
            return Result(
                check.isSuccess(),
                message,
                "Json [$value] should not equal [$other()]"
            )
        }
    }

    fun some() = object : Matcher<Option<*>> {
        override fun test(value: Option<*>): Result {
            val result = value.fold({ false }) { true }
            return Result(result, "Option [$value] should be Some", "Option [$value] should not be Some")
        }
    }
    inline fun<reified T> someOf() = object : Matcher<Option<*>> {
        override fun test(value: Option<*>): Result {
            val result = value.map { it is T }.fold({ false }) { it }
            return Result(result, "Option [$value] should be Some", "Option [$value] should not be Some")
        }
    }

    fun none() = object : Matcher<Option<*>> {
        override fun test(value: Option<*>): Result {
            val result = value.fold({ true }) { false }
            return Result(result, "Option [$value] should be None", "Option [$value] should not be None")
        }
    }

    fun ok() = object : Matcher<Either<*, *>> {
        override fun test(value: Either<*, *>): Result {
            val result = value.fold({ false }) { true }
            return Result(result, "Outcome [$value] should be OK", "Outcome [$value] should not be OK")
        }
    }

    fun err() = object : Matcher<Either<*, *>> {
        override fun test(value: Either<*, *>): Result {
            val result = value.fold({ true }) { false }
            return Result(result, "Outcome [$value] should be Err", "Outcome [$value] should not be Err")
        }
    }


}