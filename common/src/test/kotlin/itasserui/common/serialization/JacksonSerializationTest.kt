package itasserui.common.serialization

import io.kotlintest.matchers.string.haveSameLengthAs
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec

class JacksonSerializationTest : DescribeSpec({
    describe("Inline serialization") {
        lateinit var serializedForm: String
        it("Serializes the ${InlineTest::class.java.simpleName} class") {
            serializedForm = Serializer.Jackson.toJson(InlineTest("test"))
        }
        lateinit var objectForm: InlineTest
        it("Deserializes the generated string") {
            objectForm = Serializer.Jackson.fromJson(serializedForm)
        }

        it("Verifies the value") {
            objectForm.value should haveSameLengthAs("test")
        }
    }
})

data class InlineTest(val value: String)