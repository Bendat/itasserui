package itasserui.test_utils.matchers

import arrow.core.*
import arrow.data.Validated
import io.kotlintest.Matcher
import io.kotlintest.MatcherResult
import org.apache.commons.lang3.Validate
import org.skyscreamer.jsonassert.JSONAssert
import java.lang.Math.abs

val be = Be

object Be {
    fun json(strict: Boolean = false, other: () -> String) = object : Matcher<String> {
        override fun test(value: String): MatcherResult {
            val check = Try { JSONAssert.assertEquals(value, other(), strict) }
            val message = when (check) {
                is Success -> "Json [$value] should equal [$other()]"
                is Failure -> check.exception.message as String
            }
            return MatcherResult(
                check.isSuccess(),
                message,
                "Json [$value] should not equal [$other()]"
            )
        }
    }


    fun some() = object : Matcher<Option<*>> {
        override fun test(value: Option<*>): MatcherResult {
            val result = value.fold({ false }) { true }
            return MatcherResult(result, "Option [$value] should be Some", "Option [$value] should not be Some")
        }
    }

    fun <T> some(item: T) = object : Matcher<Option<T>> {
        override fun test(value: Option<T>): MatcherResult {
            val result = value.map { it == item }.fold({ false }) { true }
            return MatcherResult(result, "Option [$value] should be Some", "Option [$value] should not be Some[$item]")
        }
    }

    inline fun <reified T> someOf() = object : Matcher<Option<*>> {
        override fun test(value: Option<*>): MatcherResult {
            val result = value.map { it is T }.fold({ false }) { it }
            return MatcherResult(result, "Option [$value] should be Some", "Option [$value] should not be Some")
        }
    }

    fun none() = object : Matcher<Option<*>> {
        override fun test(value: Option<*>): MatcherResult {
            val result = value.fold({ true }) { false }
            return MatcherResult(result, "Option [$value] should be None", "Option [$value] should not be None")
        }
    }

    fun ok() = object : Matcher<Either<*, *>> {
        override fun test(value: Either<*, *>): MatcherResult {
            val result = value.fold({ false }) { true }
            return MatcherResult(result, "Outcome [$value] should be OK", "Outcome [$value] should not be OK")
        }
    }

    fun valid() = object : Matcher<Validated<*, *>> {
        override fun test(value: Validated<*, *>): MatcherResult {
            val result = value.fold({ false }) { true }
            return MatcherResult(result, "Outcome [$value] should be Valid", "Outcome [$value] should not be Valid")
        }
    }

    fun err() = object : Matcher<Either<*, *>> {
        override fun test(value: Either<*, *>): MatcherResult {
            val result = value.fold({ true }) { false }
            return MatcherResult(result, "Outcome [$value] should be Err", "Outcome [$value] should not be Err")
        }
    }

    fun closeTo(initial: Long, tolerance: Long) = object : Matcher<Long> {
        override fun test(value: Long): MatcherResult {
            val result = abs(initial - value) < tolerance
            return MatcherResult(
                result,
                "Initial $initial should be within $tolerance of $value: ${abs(initial - value)}",
                "Initial $initial should not be within $tolerance of $value ${abs(initial - value)}"
            )
        }
    }

    fun closeTo(initial: Double, tolerance: Double) = object : Matcher<Double> {
        override fun test(value: Double): MatcherResult {
            val result = abs(initial - value) < tolerance
            return MatcherResult(
                result,
                "Initial $initial should be within $tolerance of $value: ${abs(initial - value)}",
                "Initial $initial should not be within $tolerance of $value ${abs(initial - value)}"
            )
        }
    }

}