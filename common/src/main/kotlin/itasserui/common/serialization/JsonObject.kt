package itasserui.common.serialization

import org.json.JSONObject

interface JsonObject {
    fun toJson(indent: Int = 0): String =
        JSONObject(replacePasswords(Serializer.jackson.toJson(this))).toString(indent)

    private fun replacePasswords(json: String) =
        json.replace("(\\n?\\s*\"password\"\\s?:\\s?\")[^\\n\"]*(\",?\\n?)".toRegex(), "$1-password-hidden-$2")


}
