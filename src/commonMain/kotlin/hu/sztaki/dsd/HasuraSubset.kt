package hu.sztaki.dsd

//import com.kgbier.graphql.parser.GraphQLParser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

expect fun graphqlSchemaToJsonSchema(schema: String): String

/**
 * Hasura subsetting API.
 */
class HasuraSubset {

    data class CopyResult(
        val fromServerResult: String? = null,
        val fromServerError: String? = null,
        val toServerResult: String? = null,
        val toServerError: String? = null
    )

    data class UpsertResult(
        val graphql: String,
        val variables: String
    )

    data class OnConflict(
        val constraint: String,
        val columns: List<String>
    )

    /**
     * @param jsonString the JSON to turn into a Hasura upsert
     * @param onConflict optional function to generate OnConflict values for a given type.
     */
    @Throws(HasuraSubsetException::class)
    fun jsonToUpsert(
        graphqlQuery: String,
        jsonString: String,
        onConflict: (typeName: String) -> OnConflict,
        insertName: ((typeName: String) -> String)? = null
    ) : UpsertResult
    {
        val json = Json.parseToJsonElement(jsonString)

        var jsonSchema = graphqlSchemaToJsonSchema("""
            "A ToDo Object"
            type Todo {
                "A unique identifier"
                id: String!
                name: String!
                completed: Boolean
                color: Color
                "A required list containing colors that cannot contain nulls"
                requiredColors: [Color!]!
                "A non-required list containing colors that cannot contain nulls"
                optionalColors: [Color!]
                fieldWithOptionalArgument(
                  optionalFilter: [String!]
                ): [String!]
                fieldWithRequiredArgument(
                  requiredFilter: [String!]!
                ): [String!]
            }
            ""${'"'}
            A type that describes ToDoInputType. Its description might not
            fit within the bounds of 80 width and so you want MULTILINE
            ""${'"'}
            input TodoInputType {
                name: String!
                completed: Boolean
                color: Color=RED
            }
            enum Color {
              "Red color"
              RED
              "Green color"
              GREEN
            }
            type Query {
                todo(
                    "todo identifier"
                    id: String!
                    isCompleted: Boolean=false
                    requiredStatuses: [String!]!
                    optionalStatuses: [String!]
                ): Todo!
                todos: [Todo!]!
            }
            type Mutation {
                update_todo(id: String!, todo: TodoInputType!): Todo
                create_todo(todo: TodoInputType!): Todo
            }
        """.trimIndent())
//        val result = GraphQLParser.parseWithResult(graphqlQuery)

        val root = if (json is JsonObject) json else throw HasuraSubsetException("json in not an object")
        val data = json["data"] as JsonObject? ?: throw HasuraSubsetException("key 'data' is not found in root")

        var upsertVariables = mutableMapOf<String, JsonElement>()
        var upsertGraphql = StringBuilder()
        var upsertGraphqlOperations = StringBuilder()

        upsertGraphql.append("mutation hasuraSubset(")
        var needsComma = false
        // Handle top level
        data.keys.forEach {key ->
            val value = data[key]
            var dataAndConflict: DataAndConflict? = null
            when(value) {
                is JsonArray -> {
                    val list = mutableListOf<JsonObject>()
                    for (jsonElement in value) {
                        dataAndConflict = converToUpsert(jsonElement as JsonObject, onConflict)
                        list.add(dataAndConflict.data as JsonObject)
                    }
                    upsertVariables["objects_$key"] = JsonArray(list)
                    upsertVariables["on_conflict_$key"] = dataAndConflict!!.conflict
                }
                is JsonObject -> {
                    dataAndConflict = converToUpsert(value, onConflict)
                    upsertVariables["objects_$key"] = dataAndConflict.data
                    upsertVariables["on_conflict_$key"] = dataAndConflict.conflict
                }
                is JsonNull -> throw HasuraSubsetException("Null value not expected here: $value")
                is JsonPrimitive -> throw HasuraSubsetException("Primitive value not expected here: $value")
            }

            if (needsComma) {
                upsertGraphql.append(", ")
                needsComma = false
            }

            // Either use the name provided by the insertName() function or generate the default hasura insert name
            // derived from the type name
            val insert = if (insertName != null) insertName(dataAndConflict!!.type) else "insert_${dataAndConflict!!.type}"
            upsertGraphql.append("\$objects_$key: [${dataAndConflict!!.type}_insert_input!]!, \$on_conflict_$key: ${dataAndConflict.type}_on_conflict")
            upsertGraphqlOperations.append("""
                ${key}: $insert(objects: ${"$"}objects_$key, on_conflict: ${"$"}on_conflict_$key) {
                    affected_rows
                }
                
            """.trimIndent())
        }

        upsertGraphql.append(") {\n")
        upsertGraphql.append(upsertGraphqlOperations);
        upsertGraphql.append("}\n")

        return UpsertResult(upsertGraphql.toString(), Json.encodeToString(upsertVariables))
    }

    data class DataAndConflict(
        val data: JsonElement,
        val conflict: JsonObject,
        val type: String
    )

    private fun converToUpsert(jsonObj: JsonObject, onConflict: (typeName: String) -> OnConflict): DataAndConflict
    {
        fun createConflict(typeName: String): JsonObject
        {
            val conflictValues = onConflict(typeName)
            val conflict = JsonObject(mapOf(
                "constraint" to JsonPrimitive(conflictValues.constraint),
                "update_columns" to JsonArray(conflictValues.columns.map { JsonPrimitive(it) })
            ))
            return conflict
        }

        fun createDataAndOnConflict(value: JsonElement) : DataAndConflict
        {
            var data : JsonElement
            var typeInValue = ""
            when(value) {
                is JsonObject -> {
                    typeInValue = (value["__typename"] as JsonPrimitive).content
                    data = converToUpsert(value, onConflict).data
                }
                is JsonArray ->
                    data = JsonArray(value.map {
                        typeInValue = ((it as JsonObject)["__typename"] as JsonPrimitive).content
                        converToUpsert(it, onConflict).data
                    })
                else -> throw HasuraSubsetException("Invalid value: $value")
            }
            return DataAndConflict(data, createConflict(typeInValue), typeInValue)
        }

        val upsert = mutableMapOf<String, JsonElement>()
        jsonObj.forEach { entry ->
            when (entry.value) {
                // Simple value: copy as is
                is JsonPrimitive -> if (entry.key != "__typename") upsert[entry.key] = entry.value
                // convert to  {data:{...}, on_conflict:{...}}
                // or {data:[...], on_conflict:{...}}
                else -> {
                    val dataAndConflict = createDataAndOnConflict(entry.value)
                    upsert[entry.key] = JsonObject(mapOf(
                        "data" to dataAndConflict.data,
                        "on_conflict" to dataAndConflict.conflict
                    ))
                }
            }
        }

        val type = (jsonObj["__typename"] as JsonPrimitive).content

        return DataAndConflict(JsonObject(upsert), createConflict(type), type)
    }

    fun copy(graphql: String, fromServer: HasuraServer, toServer: HasuraServer): CopyResult
    {
        TODO()
    }

}

class HasuraSubsetException(message: String, cause: Throwable? = null) : Exception(message, cause)
