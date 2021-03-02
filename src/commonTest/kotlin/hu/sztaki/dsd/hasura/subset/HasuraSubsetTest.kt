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
    fun test_processGraphql() {
        println("***** graphqlPreprocessingWorks starts")
        val hasuraSubset = HasuraSubset()
        val result = hasuraSubset.processGraphql(tweetQuery, tweetGraphql)
        println("test_processGraphql $result")
        assertEquals(tweetQueryExpaned, result)
    }
}
