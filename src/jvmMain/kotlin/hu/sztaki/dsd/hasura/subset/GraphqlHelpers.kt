package hu.sztaki.dsd.hasura.subset

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.idl.SchemaGenerator
import graphql.GraphQL
import graphql.parser.Parser
import com.fasterxml.jackson.databind.SerializationFeature

actual fun graphqlSchemaToJsonSchema(schema: String): String {
    println(""" parseGraphqlSchema in JVM called with '${schema}'""")
    return "TODO"
}

actual fun graphqlSchemaToIntrospectedSchema(schema: String): String
{
//    val typeDefinitionRegistry = SchemaParser().parse(schema)
//    val graphQLSchema = SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, RuntimeWiring.MOCKED_WIRING)
//
//    val schemaParser = SchemaParser()
//    val typeDefinitionRegistry = schemaParser.parse(schema)
//
//    val schemaGenerator = SchemaGenerator.
//    schemaGenerator.
//    val graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, newRuntimeWiring().build())

    val graphQLSchema = SchemaGenerator.createdMockedSchema(schema)
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