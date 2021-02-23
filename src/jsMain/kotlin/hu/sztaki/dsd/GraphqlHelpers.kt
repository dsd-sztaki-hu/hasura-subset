package hu.sztaki.dsd

import hu.sztaki.dsd.graphql.buildSchema
import hu.sztaki.dsd.graphql.getIntrospectionQuery
import hu.sztaki.dsd.graphql.graphqlSync
import hu.sztaki.dsd.graphql2JsonSchema.fromIntrospectionQuery

// JSON.stringify(value[, replacer[, space]])
external class JSON {
    companion object {
        fun stringify(json: dynamic, replacer: dynamic = definedExternally, space: Int = definedExternally): String
    }
}


actual fun graphqlSchemaToJsonSchema(schema: String): String {
    val jsonSchema = fromIntrospectionQuery(graphqlSync(buildSchema(schema), getIntrospectionQuery()).data)
    //js("""JSON.stringify(jsonSchema, null, 2)""")
    return JSON.stringify(jsonSchema)
}

actual fun graphqlSchemaToIntrospectedSchema(schema: String): String
{
    val intro = graphqlSync(buildSchema(schema), getIntrospectionQuery()).data["__schema"]
    // Note: with JSON.stringify(intro, null, 2) printlning the json fails in tests, this must be a bug in Kotlin
    val json = JSON.stringify(intro)
    return json
}
