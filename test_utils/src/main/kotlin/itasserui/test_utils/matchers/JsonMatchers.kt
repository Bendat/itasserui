package itasserui.test_utils.matchers

import arrow.core.*
import io.kotlintest.Matcher
import io.kotlintest.Result
import org.skyscreamer.jsonassert.JSONAssert
import java.lang.Math.abs

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

    fun <T> some(item: T) = object : Matcher<Option<T>> {
        override fun test(value: Option<T>): Result {
            val result = value.map { it == item }.fold({ false }) { true }
            return Result(result, "Option [$value] should be Some", "Option [$value] should not be Some[$item]")
        }
    }

    inline fun <reified T> someOf() = object : Matcher<Option<*>> {
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

    fun closeTo(initial: Long, tolerance: Long) = object : Matcher<Long> {
        override fun test(value: Long): Result {
            val result = abs(initial - value) < tolerance
            return Result(
                result,
                "Initial $initial should be within $tolerance of $value: ${abs(initial - value)}",
                "Initial $initial should not be within $tolerance of $value ${abs(initial - value)}"
            )
        }

    }
}