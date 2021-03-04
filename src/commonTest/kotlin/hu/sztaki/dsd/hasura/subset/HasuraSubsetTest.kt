package hu.sztaki.dsd.hasura.subset

import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.file.std.uniVfs
import io.ktor.client.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class HasuraSubsetTest {

    @Test
    fun test_jsonToUpsert() {
        val hasuraSubset = HasuraSubset()

        for (test in tests) {
            println("Running test: "+test.description)
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
        var result = hasuraSubset.processGraphql(tweetQuery, tweetGraphql)
        println("test_processGraphql 1: $result")
        assertEquals(tweetQueryExpaned, result)

        // Should be the same as without the false
        result = hasuraSubset.processGraphql(tweetQuery, tweetGraphql, false)
        println("test_processGraphql 1.1: $result")
        assertEquals(tweetQueryExpaned, result)

        result = hasuraSubset.processGraphql(tweetQuery, tweetGraphql, true)
        println("test_processGraphql 2: $result")
        assertEquals(tweetQueryExpanedAlwaysIncludeTypeName, result)

    }

    @ExperimentalTime
    @Test
    fun test_processGraphql_big_schema() = suspendTest {
        val hasuraSubset = HasuraSubset()
        val schemaFile = "$testResourceDir/mtmt2.graphql".uniVfs
        val schema = schemaFile.readString()
        var timedResult = measureTime {
            val result = hasuraSubset.processGraphql(graphqlQueryExampleMtmt, schema)
            println("test_processGraphql_big_schema input:\n$graphqlQueryExampleMtmt")
            println("test_processGraphql_big_schema result:\n$result")
            assertEquals(graphqlQueryExampleMtmtExpanded, result)
        }
        println("timedResult1: "+timedResult)

        timedResult = measureTime {
            hasuraSubset.processGraphql(graphqlQueryExampleMtmt, schema)
        }
        // Must be much faster, because graphql schema is not reparsed
        println("timedResult2: "+timedResult)
    }

    @Test
    fun test_executeGraphqlQuery() = suspendTest {
        val hasuraSubset = HasuraSubset()
        val schemaFile = "$testResourceDir/mtmt2.graphql".uniVfs
        val schema = schemaFile.readString()

        val processedQuery = hasuraSubset.processGraphql(graphqlQueryExampleMtmt, schema, true)

        val result = hasuraSubset.executeGraphqlQuery(
            processedQuery,
            mapOf("param" to 3156695),
            HasuraServer(
                HttpClient(),
                "http://localhost:8834/v1/graphql",
                "mtmt2"
            )
        )
        println(result.prettifiedJson)

        val upsertRes = hasuraSubset.jsonToUpsert(
            processedQuery,
            result,
            {
                HasuraSubset.OnConflict(
                    "${it}_pkey",
                    listOf("mtid")
                )
            }
        )

        println("upsertRes.graphql: "+upsertRes.graphql)
        println("upsertRes.variables" + upsertRes.variables.prettifiedJson)

    }
}
