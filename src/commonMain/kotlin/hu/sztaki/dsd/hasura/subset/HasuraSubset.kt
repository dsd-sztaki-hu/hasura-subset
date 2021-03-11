package hu.sztaki.dsd.hasura.subset

import com.kgbier.graphql.parser.GraphQLParser
import com.kgbier.graphql.parser.structure.*
import com.kgbier.graphql.printer.GraphQLPrinter
import com.soywiz.krypto.MD5
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

expect fun graphqlSchemaToJsonSchema(schema: String): String

/**
 * Given a graphql schema returns its introspected JSON version as a string. This JSON is then can be processed here
 * by the common code.
 */
expect fun graphqlSchemaToIntrospectedSchema(schema: String): String

expect fun graphqlQueryToAst(query: String): String

/**
 * Hasura subsetting tool.
 */
class HasuraSubset {

    data class CopyResult(
        val fromServerResult: String? = null,
        val fromServerError: String? = null,
        val toServerResult: String? = null,
        val toServerError: String? = null
    )

    data class UpsertResult(
        val mutation: String,
        val variables: String
    )

    data class OnConflict(
        val constraint: String,
        val columns: List<String>
    )

    val cachedSchemas = mutableMapOf<String, JsonObject>()

    /**
     * Process graphql query by extending hasura-subset macros.
     *
     * - ...everything: expands all simple fields of object
     */
    fun processGraphql(
        graphqlQuery: String,
        graphqlSchema: String,
        alwaysIncludeTypeName: Boolean = false
    ) : String
    {
        // Try to reuse cached version
        val hash = MD5.digest(graphqlSchema.encodeToByteArray()).hex
        var schemaDocument = cachedSchemas[hash] ?: Json.decodeFromString(graphqlSchemaToIntrospectedSchema(graphqlSchema))
        if (cachedSchemas[hash] == null) {
            cachedSchemas.put(hash, schemaDocument)
        }

        val queryAST = GraphQLParser.parseWithResult(graphqlQuery)
        if (queryAST.match == null) {
            throw HasuraSubsetException("Error while parsing query at ${queryAST.rest.state.row}:${queryAST.rest.state.col}")
        }

        // We have a match, process it
        queryAST.match.apply {
            // Get the operation from the query
            val op = queryOperations[0]

            // Find top level selection and starting there recursively expand __everything in all subsequent
            // selection lists
            op.selectionSet.forEach {
                when(it) {
                    is SelectionField -> {
                        val queryOp = it.selection.name

                        // Find operation type in schema
                        val opInIntro = schemaDocument.findQueryOperation(queryOp)
                            ?: throw HasuraSubsetException("No query operation type ${op.name} found in graphql schema")

                        val opTypeName = opInIntro.baseType.stringValue("name")
                        val selection = op.selectionSet[0].selection ?: return@forEach
                        expandEverythingSelection(selection, opTypeName, alwaysIncludeTypeName, schemaDocument, true)
                    }
                    is SelectionFragmentSpread -> TODO()
                    is SelectionInlineFragment -> TODO()
                }
            }
            return GraphQLPrinter().print(this)
        }
    }

    /**
     * Expand `__everything` in the selectionSet of `target` and all its subsequent selection sets recursively
     * with the scalar fields of the type of the selection. This lets us use __everything to list all simple fields
     * and so we don't have to always handpick fields. This is also comes handy when schema changes as we don't have
     * to update when a simple field changes.
     */
    fun expandEverythingSelection(
        target: WithSelectionSet,
        typeName: String,
        alwaysIncludeUUTypeName: Boolean,
        schemaDocument: JsonObject,
        recurse: Boolean = true
    )
    {
        val opTypeDefinition = schemaDocument.getType(typeName)
        if (opTypeDefinition == null) {
            HasuraSubsetException("No type definition ${opTypeDefinition} found in graphql schema")
        }

        // Do we have an __everything selection
        var everythingSelection = target.selectionSet.filter {
            when(it) {
                is SelectionField -> {
                    it.selection.name.toLowerCase() == "__everything"
                }
                is SelectionFragmentSpread -> TODO()
                is SelectionInlineFragment -> TODO()
            }
        }

        // Collect all scalars except those starting with __ (except __everythig). Ie. __typename is only
        // included in final list if explicitly set, __everything won't generate it on its own.
        var scalarSelections = target.selectionSet.filter {
            if (it.selection is Field) {
                val sel = it.selection as Field
                // Note: keep any starting with __ except '__everything'
                sel.selectionSet.isEmpty() &&
                        (!sel.name.startsWith("__") || sel.name.toLowerCase().startsWith("__everything"))
            }
            else {
                false
            }
        }

        // Expand __everything
        if (!everythingSelection.isEmpty()) {
            // Handle "except" argument
            var ignoreFields = mutableSetOf<String>()
            if (!(everythingSelection[0] as SelectionField).selection.arguments.isEmpty()) {
                (everythingSelection[0] as SelectionField).selection.arguments.forEach {
                    if (it.name == "except") {
                        (it.value as Value.ValueList).value.forEach {
                            if (it is Value.ValueString) {
                                ignoreFields.add(it.value)
                            }
                            else if (it is Value.ValueEnum) {
                                ignoreFields.add(it.value)
                            }
                        }
                    }
                }
            }

            val remaining = mutableListOf<Selection>()
            remaining.addAll(target.selectionSet)

            remaining.remove(everythingSelection)
            if (!scalarSelections.isEmpty()) {
                remaining.removeAll(scalarSelections)
            }

            val finalList = mutableListOf<Selection>()
            opTypeDefinition!!.scalarFieldNames
                .filter {
                    !ignoreFields.contains(it)
                }
                .forEach {name ->
                    finalList.add(SelectionField(Field(null, name, emptyList(), emptyList(), emptyList())))
                }
            finalList.addAll(remaining)
            if (alwaysIncludeUUTypeName) {
                val uutypeName = finalList.find {
                    if (it.selection is Field) {
                        (it.selection as Field).name == "__typename"
                    }
                    else {
                        false
                    }
                }
                if (uutypeName == null) {
                    finalList.add(0, SelectionField(Field(null, "__typename", emptyList(), emptyList(), emptyList())))
                }
            }
            target.selectionSet = finalList
        }

        // Recurse into complex selections
        if (recurse) {
            target.selectionSet.forEach {
                if (it.selection is Field) {
                    val field = it.selection as Field
                    if (!field.selectionSet.isEmpty()) {
                        val fieldTypeName = opTypeDefinition!!.typeNameOfField(field.name)
                        expandEverythingSelection(field, fieldTypeName!!, alwaysIncludeUUTypeName, schemaDocument, recurse)
                    }
                }
            }
        }
    }

    /**
     * Executes a graphql query  on a given server.
     */
    suspend fun executeGraphqlOperation(
        query: String,
        variables: Map<String, Any>,
        server: HasuraServer
    ): String {
        return executeGraphqlOperation(query, variables.toJsonObject(), server);
    }

    suspend fun executeGraphqlOperation(
        query: String,
        variables: String,
        server: HasuraServer
    ): String {
        return executeGraphqlOperation(query, Json.decodeFromString<JsonObject>(variables), server);

    }

    suspend fun executeGraphqlOperation(
        query: String,
        variables: JsonObject,
        server: HasuraServer
    ): String {
        val graphqlJson = Json.encodeToString(buildJsonObject {
            put("query", query)
            put("variables", variables)
        })

        return server.client.post {
            url (server.url)
            headers {
                set("X-Hasura-Admin-Secret", server.adminSecret)
            }
            body = graphqlJson
        }
    }

    /**
     * @param queryResultJson the JSON to turn into a Hasura upsert
     * @param onConflict optional function to generate OnConflict values for a given type.
     */
    @Throws(HasuraSubsetException::class)
    fun jsonToUpsert(
        queryResultJson: String,
        onConflict: (typeName: String) -> OnConflict,
        insertName: ((typeName: String) -> String)? = null
    ) : UpsertResult
    {
        val json = Json.parseToJsonElement(queryResultJson)

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
            upsertGraphql.append("\$objects_$key: [${dataAndConflict.type}_insert_input!]!, \$on_conflict_$key: ${dataAndConflict.type}_on_conflict")
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

val Selection.selection: WithSelectionSet?
    get() {
        return when(this) {
            is SelectionField -> this.selection
            is SelectionFragmentSpread -> null
            is SelectionInlineFragment -> this.selection
        }
    }

class HasuraSubsetException(message: String, cause: Throwable? = null) : Exception(message, cause)

//fun String.buildJsonObject(other: Map<String, Any?>) : JsonElement {
//    val jsonEncoder = Json{ encodeDefaults = true } // Set this accordingly to your needs
//    val map = emptyMap<String, JsonElement>().toMutableMap()
//
//    other.forEach {
//        map[it.key] = if (it.value != null)
//            jsonEncoder.encodeToJsonElement(serializer(it.value!!::class.starProjectedType), it.value)
//        else JsonNull
//    }
//
//    return JsonObject(map)
//}

private val graphqlSchemaExample = """
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
        """.trimIndent()