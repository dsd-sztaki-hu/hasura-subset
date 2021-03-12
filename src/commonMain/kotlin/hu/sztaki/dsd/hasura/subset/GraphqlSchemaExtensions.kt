package hu.sztaki.dsd.hasura.subset

import kotlinx.serialization.json.*

/**
 * Find a query operatio in a Document JsonObject
 */
fun JsonObject.findQueryOperation(name: String): JsonObject? {
    val queryTypeName = (this["queryType"] as JsonObject).stringValue("name")
    val queryType = getType(queryTypeName)!!
    val ops = queryType.getField(name)
    return ops
}

/**
 * Get graphql type by name from a Document.typess
 */
fun JsonObject.getType(name: String): JsonObject? {
    val types = get("types") as JsonArray
    val type = types.find {
        val type = it as JsonObject
        type.stringValue("name") == name
    } as JsonObject?
    return type
}

/**
 * Get type name of the type from Document.types
 */
fun JsonObject.getTypeName(name: String): String? {
    val type = getType(name) ?: return null
    return type.stringValue("name")
}


fun JsonObject.stringValue(name: String) =
    (this[name] as JsonPrimitive).content

/**
 * Returns field names for a graphql type object
 */
val JsonObject.fieldNames: List<String>
    get() {
        val fields = this["fields"] as JsonArray
        return fields.map {
            (it as JsonObject).stringValue("name")
        }
    }

/**
 * Name of the field type
 */
val JsonObject.fieldTypeName: String
    get() {
        // Can be NON_NULL or the actual type
        var actualType = this.baseType
        return actualType.stringValue("name")
    }

val JsonObject.hasOfType: Boolean
    get() = this["ofType"] != null && !(this["ofType"] is JsonPrimitive)

/**
 * Get a field of a graphql object type
 */
fun JsonObject.getField(name: String): JsonObject? {
    return (this["fields"] as JsonArray).find {
        (it as JsonObject).stringValue("name") == name
    } as JsonObject?
}

fun JsonObject.getInputField(name: String): JsonObject? {
    return (this["inputFields"] as JsonArray).find {
        (it as JsonObject).stringValue("name") == name
    } as JsonObject?
}


/**
 * Type of a field of a graphql object type
 */
fun JsonObject.typeOfField(name: String): JsonObject? {
    val field = getField(name)
    if (field == null) {
        return null
    }
    return field.baseType
}

/**
 * Type name of a field of a graphql object type
 */
fun JsonObject.typeNameOfField(name: String): String? {
    val type = typeOfField(name)
    if (type == null) {
        return null
    }
    return type.stringValue("name")
}



/**
 * SCALAR or OBJECT
 */
val JsonObject.fieldKind: String
    get() {
        // Can be NON_NULL or the actual type
        var actualType = this.baseType
        return actualType.stringValue("kind")
    }

/**
 * Returns field names for a graphql type object
 */
val JsonObject.scalarFieldNames: List<String>
    get() {
        val fields = this["fields"] as JsonArray
        return fields
            .filter {
                (it as JsonObject).fieldKind == "SCALAR"
            }
            .map {
                (it as JsonObject).stringValue("name")
            }
    }

/**
 * Returns base type of an argument
 */
fun JsonObject.baseTypeOfArg(name: String): JsonObject? {
    val args = this["args"]
    if (args == null) {
        return null
    }
    return (args as JsonArray).find { arg ->
        arg.jsonObject["name"]!!.jsonPrimitive.content == name
    }!!.jsonObject.baseType
}


fun JsonObject.graphqlTypeOfArg(name: String): String? {
    val args = this["args"]
    if (args == null) {
        return null
    }
    return (args as JsonArray).find { arg ->
        arg.jsonObject["name"]!!.jsonPrimitive.content == name
    }!!.jsonObject["type"]!!.jsonObject.graphqlType
}

val JsonObject.graphqlType: String
    get() {
        var res = ""
        var kind = this["kind"]!!.jsonPrimitive.content
        when(kind) {
            "NON_NULL" ->
                res = this.hasOfType.let {
                    this["ofType"]!!.jsonObject.graphqlType
                }+"!"
            "LIST" ->
                res = "[" + this.hasOfType.let {
                    this["ofType"]!!.jsonObject.graphqlType
                }+ "]"
            else ->
                res = this["name"]!!.jsonPrimitive.content
        }
        return res
    }




/**
 * For any object that has a "type" get the base type. The base type is the root
 * type with no more ofType field
 */
val JsonObject.baseType: JsonObject
    get() {
        val type = this["type"] as JsonObject
        var actual: JsonObject? = type
        if (type.hasOfType) {
           actual = type.ofType
        }
        return actual!!
    }

/**
 * Follows ofType hierarchy until reaches the end of it without ofType
 */
val JsonObject.ofType: JsonObject
    get() {
        val type = this["ofType"] as JsonObject
        if (type.hasOfType) {
            return type.ofType
        }
        return type
    }
