package hu.sztaki.dsd

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class HasuraSubsetTest {

    @Test
    fun testIfJsonToUpsertWorks() {
        val hasuraSubset = HasuraSubset()

        for (test in tests) {
            println("Running test: "+test.description)
            val typeName = "some_type"
            val result = hasuraSubset.jsonToUpsert(
                test.graphql,
                test.jsonResult,
                {
                    HasuraSubset.OnConflict(
                        "${it}_pkey",
                        listOf("mtid")
                    )
                }
            )
            println(result.graphql)
            println(result.variables)

            assertEquals(test.expectedGraphql, result.graphql)
            assertEquals(test.expectedVariables, result.variables)
        }
    }

    @Test
    fun `graphql preprocessing works`() {
        val hasuraSubset = HasuraSubset()
        val result = hasuraSubset.processGraphql("...", graphqlSchemaExample)
        println("processGraphql " + result)
    }
}

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