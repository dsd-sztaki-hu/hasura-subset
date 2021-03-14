package hu.sztaki.dsd.hasura.subset

import com.kgbier.graphql.parser.GraphQLParser
import com.kgbier.graphql.parser.structure.*
import com.kgbier.graphql.printer.GraphQLPrinter
import com.soywiz.korio.file.std.uniVfs
import com.soywiz.krypto.MD5
import io.ktor.client.request.*
import kotlinx.serialization.*
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

    private data class Include(
        var file: String,
        var recurse: Int = 1
    )

    private data class DirectiveProcessingState(
        val target: WithSelectionSet,
        val typeName: String,
        val alwaysIncludeUUTypeName: Boolean,
        val schemaDocument: JsonObject,
        val recurse: Boolean = true,
        val includeRoot: String? = null,
        val includeStack: MutableList<Include> = mutableListOf()
    )

    val cachedSchemas = mutableMapOf<String, JsonObject>()

    /**
     * Converts `graphqlSchema` to a JsonObject. The schema is cached so next invocation of
     * schemaAsJson with the same `graphqlSchema` will use the cached version.s
     */
    private fun schemaAsJson(graphqlSchema: String): JsonObject
    {
        val hash = MD5.digest(graphqlSchema.encodeToByteArray()).hex
        var schemaDocument = cachedSchemas[hash] ?: Json.decodeFromString(graphqlSchemaToIntrospectedSchema(graphqlSchema))
        if (cachedSchemas[hash] == null) {
            cachedSchemas.put(hash, schemaDocument)
        }
        return schemaDocument
    }

    /**
     * Process graphql query by extending hasura-subset macros.
     *
     * - ...everything: expands all simple fields of object
     */
     suspend fun processGraphql(
        graphqlQuery: String,
        graphqlSchema: String,
        alwaysIncludeTypeName: Boolean = false,
        includeRoot: String? = null
    ) : String
    {
        var schemaDocument = schemaAsJson(graphqlSchema)

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
                        processSubsetDirectives(DirectiveProcessingState(it.selection, opTypeName, alwaysIncludeTypeName, schemaDocument, true, includeRoot))
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
    suspend private fun processSubsetDirectives(
        state: DirectiveProcessingState,
    )
    {
        var(target,
            typeName,
            alwaysIncludeUUTypeName,
            schemaDocument,
            recurse,
            includeRoot,
            includeStack
        ) = state

        processIncludes(state)

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
                        processSubsetDirectives(state.copy(target = field, typeName = fieldTypeName!!))
                    }
                }
            }
        }
    }

    suspend private fun processIncludes(
        state: DirectiveProcessingState
    )
    {
        val target = state.target
        val includeRoot = state.includeRoot

        //
        // Collect __include
        //
        var includes = target.selectionSet.filter {
            when(it) {
                is SelectionField -> {
                    it.selection.name.toLowerCase() == "__include"
                }
                is SelectionFragmentSpread -> TODO()
                is SelectionInlineFragment -> TODO()
            }
        }

        //
        // Process includes
        //
        includes.forEach { sel ->
            if (!(sel as SelectionField).selection.arguments.isEmpty()) {
                (sel as SelectionField).selection.arguments.forEach { arg ->
                    if (arg.name == "files") {
                        if (!(arg.value is Value.ValueList)) {
                            throw HasuraSubsetException("__include(files: ...) must have an array argument")
                        }

                        (arg.value as Value.ValueList).value.forEach { argValue ->
                            if (!(argValue is Value.ValueString)) {
                                throw HasuraSubsetException("__include value is not a string: $argValue")
                            }
                            var path = argValue.value
                            if (!path.startsWith("/")) {
                                path = if(includeRoot != null) includeRoot+"/"+path else path
                            }
                            val incFile = path.uniVfs
                            val incContent = incFile.readString()
                            //println("incContent $incContent")
                            val parseRes = GraphQLParser.parseWithResult(incContent)
                            if (parseRes.match == null) {
                                throw HasuraSubsetException("Unable to parse included file as $path. Error at: ${parseRes.rest.state.row}:${parseRes.rest.state.col}")
                            }
                            mergeIncluded(target, parseRes.match)
                        }
                    }
                }
            }
        }
    }

    /**
     * Merge into a target query the selection set of the included.
     */
    private fun mergeIncluded(
        target: WithSelectionSet,
        included: Document
    )
    {
        val merged = mutableListOf<Selection>()

        // Add all to final result except __include fields
        merged.addAll(target.selectionSet.filter {
            !(it is SelectionField && it.selection.name == "__include")
        })

        included.definitions.forEach {
            val op = ((it as DefinitionExecutable).definition as ExecutableDefinitionOperation).definition as OperationDefinitionSelectionSet
            merged.addAll(op.selectionSet)
        }

        target.selectionSet = merged
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
        graphqlSchema: String,
        onConflict: (typeName: String) -> OnConflict,
        insertName: ((typeName: String) -> String)? = null,
        ignoreNullsAndEmpty: Boolean = true
    ) : UpsertResult
    {
        val schemaDocument = schemaAsJson(graphqlSchema)
        val cleanJson = if (ignoreNullsAndEmpty) queryResultJson.withoutJsonNullAndEmptyValues else queryResultJson
        var json = Json {
            encodeDefaults = false
            coerceInputValues
        }.parseToJsonElement(cleanJson)

        json = if (json is JsonObject) json else throw HasuraSubsetException("json in not an object")
        val data = json["data"] as JsonObject? ?: throw HasuraSubsetException("key 'data' is not found in root")

        var upsertVariables = mutableMapOf<String, JsonElement>()
        var upsertGraphql = StringBuilder()
        var upsertGraphqlOperations = StringBuilder()

        upsertGraphql.append("mutation hasuraSubset(")
        var needsComma = false
        // Handle top level
        data.keys.forEach {key ->
            val value = data[key]

            // Calculate top level types
            val topType = when(value) {
                is JsonArray -> {
                    value[0].jsonObject["__typename"]!!.jsonPrimitive.content
                }
                is JsonObject -> {
                    value["__typename"]!!.jsonPrimitive.content
                }
                is JsonNull -> throw HasuraSubsetException("Null value not expected here: $value")
                is JsonPrimitive -> throw HasuraSubsetException("Primitive value not expected here: $value")
                else -> throw HasuraSubsetException("Null value not expected here: $value")
            }
            // Either use the name provided by the insertName() function or generate the default hasura insert name
            // derived from the type name
            val insert = if (insertName != null) insertName(topType) else "insert_$topType"
            val insertType = schemaDocument.getType("mutation_root")!!.getField(insert)
            if (insertType == null) {
                throw HasuraSubsetException("Mutation '$insert' not found in mutation_rooot")
            }
            val objectsType = insertType.graphqlTypeOfArg("objects")
                ?: throw HasuraSubsetException("No argument 'objects' found for mutation '$insert'")
            val onConflictType = insertType.graphqlTypeOfArg("on_conflict")
                ?: throw HasuraSubsetException("No argument 'on_conflict' found for mutation '$insert'")

            val insertInputTypeName = insertType.baseTypeOfArg("objects")!!["name"]!!.jsonPrimitive.content

            // Calculate data and optional conflict values recursively
            var dataAndConflict: DataAndConflict? = null
            when(value) {
                is JsonArray -> {
                    val list = mutableListOf<JsonObject>()
                    for (jsonElement in value) {
                        dataAndConflict = convertToUpsert(jsonElement as JsonObject, insertInputTypeName, schemaDocument, onConflict)
                        list.add(dataAndConflict.data as JsonObject)
                    }
                    upsertVariables["objects_$key"] = JsonArray(list)
                    dataAndConflict!!.conflict?.let {
                        upsertVariables["on_conflict_$key"] = dataAndConflict!!.conflict!!
                    }
                }
                is JsonObject -> {
                    dataAndConflict = convertToUpsert(value, insertInputTypeName, schemaDocument, onConflict)
                    upsertVariables["objects_$key"] = dataAndConflict.data
                    dataAndConflict.conflict?.let {
                        upsertVariables["on_conflict_$key"] = dataAndConflict.conflict!!
                    }
                }
                is JsonNull -> throw HasuraSubsetException("Null value not expected here: $value")
                is JsonPrimitive -> throw HasuraSubsetException("Primitive value not expected here: $value")
            }

            if (needsComma) {
                upsertGraphql.append(", ")
                needsComma = false
            }


            // Assemble mutation
            upsertGraphql.append("\$objects_$key: $objectsType, \$on_conflict_$key: $onConflictType")
            upsertGraphqlOperations.append("""
                ${key}: $insert(objects: ${"$"}objects_$key, on_conflict: ${"$"}on_conflict_$key) {
                    affected_rows
                }
                
            """.trimIndent())
        }

        upsertGraphql.append(") {\n")
        upsertGraphql.append(upsertGraphqlOperations);
        upsertGraphql.append("}\n")

        var variablesJson = Json.encodeToString(upsertVariables)
        return UpsertResult(upsertGraphql.toString(), variablesJson)
    }

    data class DataAndConflict(
        val data: JsonElement,
        val conflict: JsonObject?,
    )

    /**
     * Converts the jsonObj, which is the result of a query to an upsert data value and an optional
     * on_conflict.
     */
    private fun convertToUpsert(
        jsonObj: JsonObject,
        insertInputTypeName: String,
        schemaDocument: JsonObject,
        onConflict: (typeName: String) -> OnConflict,
    ): DataAndConflict
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

        fun createDataAndOnConflict(value: JsonElement, inputField: JsonObject) : DataAndConflict
        {
            val inputFieldTypeName = inputField.baseType["name"]!!.jsonPrimitive.content
            val inputFieldType = schemaDocument.getType(inputFieldTypeName)!!.jsonObject

            // Type of the item(s) in the data.
            val dataInputField = inputFieldType.getInputField("data")!!
            val dataInputFieldTypeName = dataInputField.baseType["name"]!!.jsonPrimitive.content

            // Does this inputField has on_conflict? many-to-many arr_rel_insert_inputs don't have it
            // so we won't generate one for those
            val onConflictInputField = inputFieldType.getInputField("on_conflict")

            var data : JsonElement
            var typeInValue = ""
            when(value) {
                is JsonObject -> {
                    typeInValue = (value["__typename"] as JsonPrimitive).content
                    data = convertToUpsert(value, dataInputFieldTypeName, schemaDocument, onConflict).data
                }
                is JsonArray ->
                    data = JsonArray(value.map {
                        typeInValue = ((it as JsonObject)["__typename"] as JsonPrimitive).content
                        convertToUpsert(it, dataInputFieldTypeName, schemaDocument, onConflict).data
                    })
                else -> throw HasuraSubsetException("Invalid value: $value")
            }

            return DataAndConflict(
                data,
                if (onConflictInputField != null) createConflict(typeInValue) else null)
        }

        val insertInputType = schemaDocument.getType(insertInputTypeName)

        val upsert = mutableMapOf<String, JsonElement>()
        jsonObj.forEach { entry ->
            when (entry.value) {
                // Simple value: copy as is
                is JsonPrimitive -> if (entry.key != "__typename") upsert[entry.key] = entry.value
                // convert to  {data:{...}, on_conflict:{...}}
                // or {data:[...], on_conflict:{...}}
                else -> {
                    val inputField = insertInputType!!.jsonObject.getInputField(entry.key)!!
                    val dataAndConflict = createDataAndOnConflict(entry.value, inputField)
                    upsert[entry.key] = buildJsonObject {
                        put("data", dataAndConflict.data)
                        dataAndConflict.conflict?.let {
                            put("on_conflict", dataAndConflict.conflict)
                        }
                    }
                }
            }
        }

        val type = (jsonObj["__typename"] as JsonPrimitive).content

        return DataAndConflict(JsonObject(upsert), createConflict(type))
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
