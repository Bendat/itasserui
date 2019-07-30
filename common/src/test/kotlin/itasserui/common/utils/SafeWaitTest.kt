package itasserui.common.utils

import io.kotlintest.matchers.beGreaterThan
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlin.system.measureTimeMillis

class SafeWaitTest : StringSpec({
    val timeout = 300L

    "Verify safeWait blocks for $timeout milliseconds" {
        measureTimeMillis {
            safeWait(300)
        } shouldBe beGreaterThan(300)
    }
})