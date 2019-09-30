package itasserui.common.datastructures.observable

import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec

class ObservableSetWrapperTest : DescribeSpec({
    describe("Adding an duplicate element should do nothing") {
        val set = observableSetOf(1, 2, 3, 4)

        it("Verifies the set initialized correctly") {
            set.size should be(4)
        }
        it("Tries to add 1 again") {
            set.add(1) should be(false)
        }

        it("Tries verifies the size has not changed") {
            set.size should be(4)
        }


    }
})