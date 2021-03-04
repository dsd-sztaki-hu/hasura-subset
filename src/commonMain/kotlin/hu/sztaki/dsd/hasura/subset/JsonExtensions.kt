package hu.sztaki.dsd.hasura.subset

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

// Converting Map<String, Any> to JSON is unnecessarily difficult with kotlinx.serialization
// Found this solution for now:
// https://github.com/Kotlin/kotlinx.serialization/issues/746#issuecomment-737000705
fun Any?.toJsonElement(): JsonElement {
    return when (this) {
        is Number -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is Array<*> -> this.toJsonArray()
        is List<*> -> this.toJsonArray()
        is Map<*, *> -> this.toJsonObject()
        is JsonElement -> this
        else -> JsonNull
    }
}

fun Array<*>.toJsonArray(): JsonArray {
    val array = mutableListOf<JsonElement>()
    this.forEach { array.add(it.toJsonElement()) }
    return JsonArray(array)
}

fun List<*>.toJsonArray(): JsonArray {
    val array = mutableListOf<JsonElement>()
    this.forEach { array.add(it.toJsonElement()) }
    return JsonArray(array)
}

fun Map<*, *>.toJsonObject(): JsonObject {
    val map = mutableMapOf<String, JsonElement>()
    this.forEach {
        if (it.key is String) {
            map[it.key as String] = it.value.toJsonElement()
        }
    }
    return JsonObject(map)
}

/**
 * Given a string of JSON content returnd its prettified version
 */
val String.prettifiedJson: String
    get() {
        val resultJsonObj = Json.decodeFromString<JsonObject>(this)
        val pretty = Json { prettyPrint = true }.encodeToString(resultJsonObj)
        return pretty
    }