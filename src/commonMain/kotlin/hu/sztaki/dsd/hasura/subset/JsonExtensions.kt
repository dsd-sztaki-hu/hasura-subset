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

/**
 * Removes fields with null values from a JSON string
 */
val String.withoutJsonNullAndEmptyValues: String
    get() {
        val json = Json.parseToJsonElement(this)
        return Json.encodeToString(json.withoutJsonNullAndEmptyValues())
    }

/**
 * Removes fields with null values from any JSON element
 */
fun JsonElement.withoutJsonNullAndEmptyValues(): JsonElement {
    return when(this) {
        is JsonObject -> this.withoutJsonNullAndEmptyValues()
        is JsonArray -> this.withoutJsonNullAndEmptyValues()
        else -> this
    }
}

/**
 * Removes fields with null values from JSON array
 */
fun JsonArray.withoutJsonNullAndEmptyValues(): JsonArray
{
    val orig = this
    return buildJsonArray {
        orig.forEach { value ->
            when(value) {
                null -> return@forEach
                is JsonNull -> return@forEach
                is JsonPrimitive -> if (value.content != "null") add(value.toJsonElement())
                is JsonObject -> if (!value.isEmpty()) add(value.toJsonElement().withoutJsonNullAndEmptyValues())
                is JsonArray -> if (!value.isEmpty()) add(value.toJsonElement().withoutJsonNullAndEmptyValues())
            }
        }
    }
}

/**
 * Removes fields with null values from JSON object
 */
fun JsonObject.withoutJsonNullAndEmptyValues(): JsonObject
{
    val orig = this
    return buildJsonObject {
        orig.keys.forEach { key ->
            val value = orig[key]
            when(value) {
                null -> return@forEach
                is JsonNull -> return@forEach
                is JsonPrimitive -> if (value.content != "null") put(key, value.toJsonElement())
                is JsonObject -> if (!value.isEmpty()) put(key, value.toJsonElement().withoutJsonNullAndEmptyValues())
                is JsonArray -> if (!value.isEmpty()) put(key, value.toJsonElement().withoutJsonNullAndEmptyValues())
            }
        }
    }
}