package hu.sztaki.dsd.hasura.subset

import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.file.std.uniVfs
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
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
        val hasuraSubset = HasuraSubset()
        val result = hasuraSubset.processGraphql(tweetQuery, tweetGraphql)
        println("test_processGraphql $result")
        assertEquals(tweetQueryExpaned, result)
    }

    // For now, this won't work in JS
    @Test
    fun test_processGraphql_big_schema() = suspendTest {
        val hasuraSubset = HasuraSubset()
        val schemaFile = "./src/commonTest/resources/mtmt2.graphql".uniVfs
        val schema = schemaFile.readString()
        val result = hasuraSubset.processGraphql(graphqlQueryExampleMtmt, schema)
        println("test_processGraphql_big_schema input:\n$graphqlQueryExampleMtmt")
        println("test_processGraphql_big_schema result:\n$result")
        assertEquals(graphqlQueryExampleMtmtExpanded, result)
    }
}
