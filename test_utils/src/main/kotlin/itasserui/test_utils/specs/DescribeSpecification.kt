package itasserui.test_utils.specs

import io.kotlintest.specs.AbstractDescribeSpec
import io.kotlintest.specs.DescribeSpec
import itasserui.test_utils.Logger

abstract class DescribeSpecification(op: AbstractDescribeSpec.() -> Unit) :
    DescribeSpec(op), Logger