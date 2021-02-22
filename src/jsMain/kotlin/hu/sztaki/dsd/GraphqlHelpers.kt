package hu.sztaki.dsd

import hu.sztaki.dsd.graphql.buildSchema
import hu.sztaki.dsd.graphql.getIntrospectionQuery
import hu.sztaki.dsd.graphql.graphqlSync
import hu.sztaki.dsd.graphql2JsonSchema.fromIntrospectionQuery

// JSON.stringify(value[, replacer[, space]])
external class JSON {
    companion object {
        fun stringify(json: dynamic, replacer: dynamic, space: Int): String
    }
}


actual fun graphqlSchemaToJsonSchema(schema: String): String {
    val jsonSchema = fromIntrospectionQuery(graphqlSync(buildSchema(schema), getIntrospectionQuery()).data)
    //js("""JSON.stringify(jsonSchema, null, 2)""")
    val jsonSchemaString = JSON.stringify(jsonSchema, null, 2)
    println("\n\njsonSchema"+jsonSchemaString+"\n\n")
    return jsonSchemaString
}