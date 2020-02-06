//package itasserui.common.serialization
//
//import io.kotlintest.should
//import io.kotlintest.shouldNot
//import io.kotlintest.specs.StringSpec
//import itasserui.test_utils.matchers.be
//
//data class JsonTestClass(val a: String, val b: Int) : JsonObject
//
//class JsonObjectTest : StringSpec({
//    val testClass = JsonTestClass("A", 2)
//    "JsonObject should correctly serialize"{
//        testClass.toJson() should be.json { """{"a":"A", "b":2}""" }
//    }
//
//    "JsonObject should match json string when serialized" {
//        testClass.toJson() shouldNot be.json { """{"a":"A", "b":4}""" }
//    }
//
//    "JsonObject should deserialize back to its correct form" {
//        Serializer.fromJson<JsonTestClass>(testClass.toJson())
//    }
//})