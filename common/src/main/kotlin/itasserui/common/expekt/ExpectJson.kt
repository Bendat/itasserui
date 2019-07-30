package itasserui.common.expekt

import itasserui.common.serialization.JsonObject
import org.skyscreamer.jsonassert.JSONAssert

class ExpectJson(subject: JsonObject?, flavor: Flavor) : ExpectAny<JsonObject>(subject, flavor) {
    override val not: ExpectAny<JsonObject>
        get() = super.not
    override val `null`: ExpectAny<JsonObject>
        get() = super.`null`

    override fun <S : JsonObject> instanceof(type: Class<S>): ExpectJson {
        super.instanceof(type)
        return this

    }

    override fun identity(expected: JsonObject?): ExpectJson {
        super.identity(expected)
        return this
    }

    override fun equal(expected: JsonObject?): ExpectJson {
        super.equal(expected)
        return this
    }

    override fun satisfy(predicate: (a: JsonObject) -> Boolean): ExpectJson {
        super.satisfy(predicate)
        return this
    }

    override val to: ExpectJson
        get() = super.to.let { this }
    override val be: ExpectJson
        get() = super.be.let { this }
    override val been: ExpectJson
        get() = super.been.let { this }
    override val that: ExpectJson
        get() = super.that.let { this }
    override val which: ExpectJson
        get() = super.which.let { this }
    override val and: ExpectJson
        get() = super.and.let { this }
    override val has: ExpectJson
        get() = super.has.let { this }
    override val have: ExpectJson
        get() = super.have.let { this }
    override val with: ExpectJson
        get() = super.with.let { this }
    override val at: ExpectJson
        get() = super.at.let { this }
    override val a: ExpectJson
        get() = super.a.let { this }
    override val an: ExpectJson
        get() = super.an.let { this }
    override val of: ExpectJson
        get() = super.of.let { this }
    override val same: ExpectJson
        get() = super.same.let { this }
    override val the: ExpectJson
        get() = super.the.let { this }
    override val `is`: ExpectJson
        get() = super.`is`.let { this }

    fun json(other: JsonObject, strict: Boolean = true) {
        words.add("json")
        JSONAssert.assertEquals(subject?.toJson(), other.toJson(), strict)
    }

    fun json(other: String, strict: Boolean = true): ExpectJson {
        words.add("json")
        JSONAssert.assertEquals(subject?.toJson(), other, strict)
        return this
    }

}