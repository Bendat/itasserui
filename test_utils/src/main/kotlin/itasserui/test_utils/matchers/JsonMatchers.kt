package itasserui.test_utils.matchers

import arrow.core.Failure
import arrow.core.Success
import arrow.core.Try
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
}