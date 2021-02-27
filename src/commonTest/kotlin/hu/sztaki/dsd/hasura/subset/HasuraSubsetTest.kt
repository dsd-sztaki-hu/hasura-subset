package hu.sztaki.dsd.hasura.subset

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
    fun graphqlPreprocessingWorks() {
        println("***** graphqlPreprocessingWorks starts")
        val hasuraSubset = HasuraSubset()
        val result = hasuraSubset.processGraphql(graphqlQueryExample, graphqlSchemaExample)
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

val graphqlQueryExample1 = """
    query exampleQuery(${"$"}param: [bigint]!){
      publication(mtid:${"$"}param) {
        __typename
        id
      }
    }
""".trimIndent()

val graphqlQueryExample = """
            query exampleQuery(${"$"}param: [bigint!]!){
              publication(mtid:${"$"}param) {
                __typename
                dtype
                otype
                mtid
                title
                deleted
                published
                unhandledTickets
                authorCount
                citation
                citationCount
                citationCountUnpublished
                citationCountWoOther
                citedCount
                citedPubCount
                citingPubCount
                contributorCount
                core
                doiCitationCount
                independentCitationCount
                independentCitCountWoOther
                independentCitingPubCount
                oaFree
                ownerAuthorCount
                ownerInstituteCount
                publicationPending
                scopusCitationCount
                unhandledCitationCount
                unhandledCitingPubCount
                wosCitationCount                
                authorships {
                  __typename
                  dtype
                  otype
                  mtid  
                  familyName
                  givenName
                  deleted
                  published
                  unhandledTickets
                  listPosition
                  oldListPosition
                  share 
                  author {
                    __typename
                    dtype
                    otype
                    mtid
                    deleted
                    published
                    unhandledTickets 
                    authorNames {
                      __typename
                      otype
                      mtid           
                      familyName
                      givenName
                      fullName
                      deleted
                      published
                      unhandledTickets 
                    }
                  }
                }
              }
            }
            """.trimIndent()