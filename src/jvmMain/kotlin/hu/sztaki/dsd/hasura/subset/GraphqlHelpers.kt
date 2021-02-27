package hu.sztaki.dsd.hasura.subset

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.idl.TypeDefinitionRegistry
import graphql.schema.idl.SchemaParser
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.ExecutionResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

import graphql.GraphQL
import graphql.parser.Parser
import graphql.schema.idl.SchemaPrinter
import com.fasterxml.jackson.databind.SerializationFeature








actual fun graphqlSchemaToJsonSchema(schema: String): String {
    println(""" parseGraphqlSchema in JVM called with '${schema}'""")
    return "TODO"
}

actual fun graphqlSchemaToIntrospectedSchema(schema: String): String
{
    val schemaParser = SchemaParser()
    val typeDefinitionRegistry = schemaParser.parse(schema)

    val schemaGenerator = SchemaGenerator()
    val graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, newRuntimeWiring().build())

    val build = GraphQL.newGraphQL(graphQLSchema).build()
    val executionResult = build.execute(INTROSPECTION_QUERY)

    val intro = executionResult.getData() as Map<String, Any>
    return (intro["__schema"] as Map<String, Any>).toJsonObject().toString()
}

actual fun graphqlQueryToAst(query: String): String
{
    val mapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    val doc = Parser().parseDocument(query)
    //mapper.writeValueAsString(doc)
    return mapper.writeValueAsString(doc)
}


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

// This is the same as the result of graphql-json's getInstrospectionQuery()
private val INTROSPECTION_QUERY = """
    query IntrospectionQuery {
      __schema {
        
        queryType { name }
        mutationType { name }
        subscriptionType { name }
        types {
          ...FullType
        }
        directives {
          name
          description
          
          locations
          args {
            ...InputValue
          }
        }
      }
    }

    fragment FullType on __Type {
      kind
      name
      description
      
      fields(includeDeprecated: true) {
        name
        description
        args {
          ...InputValue
        }
        type {
          ...TypeRef
        }
        isDeprecated
        deprecationReason
      }
      inputFields {
        ...InputValue
      }
      interfaces {
        ...TypeRef
      }
      enumValues(includeDeprecated: true) {
        name
        description
        isDeprecated
        deprecationReason
      }
      possibleTypes {
        ...TypeRef
      }
    }

    fragment InputValue on __InputValue {
      name
      description
      type { ...TypeRef }
      defaultValue
      
      
    }

    fragment TypeRef on __Type {
      kind
      name
      ofType {
        kind
        name
        ofType {
          kind
          name
          ofType {
            kind
            name
            ofType {
              kind
              name
              ofType {
                kind
                name
                ofType {
                  kind
                  name
                  ofType {
                    kind
                    name
                  }
                }
              }
            }
          }
        }
      }
    }
""".trimIndent()