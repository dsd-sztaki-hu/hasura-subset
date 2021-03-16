package hu.sztaki.dsd.hasura.subset

import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.file.std.uniVfs
import io.ktor.client.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class HasuraSubsetTest {

//    @Test
//    fun test_jsonToUpsert() {
//        val hasuraSubset = HasuraSubset()
//
//        for (test in tests) {
//            println("Running test: "+test.description)
//            val result = hasuraSubset.jsonToUpsert(
//                test.jsonResult,
//                {
//                    HasuraSubset.OnConflict(
//                        "${it}_pkey",
//                        listOf("mtid")
//                    )
//                }
//            )
//            println(result.mutation)
//            println(result.variables)
//
//            assertEquals(test.expectedGraphql, result.mutation)
//            assertEquals(test.expectedVariables, result.variables)
//        }
//    }

    @Test
    fun test_processGraphql() = suspendTest {
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

        var result = hasuraSubset.processGraphql(
            graphqlQueryExampleMtmtWithEverythingParameters,
            schema,
            true
        )
        println("test_processGraphql_big_schema input:\n$graphqlQueryExampleMtmtWithEverythingParameters")
        println("test_processGraphql_big_schema result:\n$result")
        assertEquals(graphqlQueryExampleMtmtExpandedWithUUTypenameFiltered, result)

    }

    @Test
    fun test_executeGraphqlQuery() = suspendTest {
        val hasuraSubset = HasuraSubset()
        val schemaFile = "$testResourceDir/mtmt2.graphql".uniVfs
        val schema = schemaFile.readString()

        val processedQuery = hasuraSubset.processGraphql(
            graphqlQueryExampleMtmt,
            schema,
            true
        )

        var result = hasuraSubset.executeGraphqlOperation(
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
            result,
            schema,
            {
                HasuraSubset.OnConflict(
                    "${it}_pkey",
                    listOf("mtid")
                )
            }
        )

        println("upsertRes.graphql: "+upsertRes.mutation)
        println("upsertRes.variables" + upsertRes.variables.prettifiedJson)

        result = hasuraSubset.executeGraphqlOperation(
            upsertRes.mutation,
            upsertRes.variables,
            HasuraServer(
                HttpClient(),
                "http://localhost:8835/v1/graphql",
                "mtmt2"
            )
        )

        println("mutation result: $result")
    }

    @Test
    fun test_executeGraphqlQuery_withOptionalOnConflict() = suspendTest {
        val hasuraSubset = HasuraSubset()
        val schemaFile = "$testResourceDir/mtmt2.graphql".uniVfs
        val schema = schemaFile.readString()

        val processedQuery = hasuraSubset.processGraphql(
            graphqlQueryExampleWithRatings,
            schema,
            true
        )

        var result = hasuraSubset.executeGraphqlOperation(
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
            result,
            schema,
            {
                HasuraSubset.OnConflict(
                    "${it}_pkey",
                    listOf("mtid")
                )
            }
        )

        println("upsertRes.graphql: "+upsertRes.mutation)
        println("upsertRes.variables" + upsertRes.variables.prettifiedJson)

        result = hasuraSubset.executeGraphqlOperation(
            upsertRes.mutation,
            upsertRes.variables,
            HasuraServer(
                HttpClient(),
                "http://localhost:8835/v1/graphql",
                "mtmt2"
            )
        )

        println("mutation result: $result")
    }

    @Test
    fun test_executeGraphqlQuery_with_mixed() = suspendTest {
        val hasuraSubset = HasuraSubset()
        val schemaFile = "$testResourceDir/mtmt2.graphql".uniVfs
        val schema = schemaFile.readString()

        val processedQuery = hasuraSubset.processGraphql(
            mixedTypesInQuery,
            schema,
            true
        )

        var result = hasuraSubset.executeGraphqlOperation(
            processedQuery,
            "{}",
            HasuraServer(
                HttpClient(),
                "http://localhost:8834/v1/graphql",
                "mtmt2"
            )
        )
        println(result.prettifiedJson)

        val upsertRes = hasuraSubset.jsonToUpsert(
            result,
            schema,
            {
                HasuraSubset.OnConflict(
                    "${it}_pkey",
                    listOf("mtid")
                )
            }
        )

        println("upsertRes.graphql: "+upsertRes.mutation)
        println("upsertRes.variables" + upsertRes.variables.prettifiedJson)

        result = hasuraSubset.executeGraphqlOperation(
            upsertRes.mutation,
            upsertRes.variables,
            HasuraServer(
                HttpClient(),
                "http://localhost:8835/v1/graphql",
                "mtmt2"
            )
        )

        println("mutation result: $result")
    }

    @Test
    fun test_removeNullValues()
    {
        val json1 = """
            {
                "name": "John",
                "country": "null",
                "address": "null",
                "phoneNumbers": [
                    "123456",
                    "78910",
                    "null",
                    "123456",
                    "null",
                    "null"
                ],
                "friends": [
                    {
                        "age": "null",
                        "name": "Susan",
                        "country": "null",
                        "address": "Susan's address",   
                        "phoneNumbers": [
                            "null",
                            "11111",
                            "22222",
                            "null"
                        ]
                    },
                    {
                        "name": "George",
                        "country": "Germany",
                        "address": "null",
                        "phoneNumbers": []
                    }
                ],
                "age": 30,
                "email": "null"
            }
        """.trimIndent()

        val result1 = """
            {"name":"John","phoneNumbers":["123456","78910","123456"],"friends":[{"name":"Susan","address":"Susan's address","phoneNumbers":["11111","22222"]},{"name":"George","country":"Germany"}],"age":30}
        """.trimIndent()

        val json2 = json1.withoutJsonNullAndEmptyValues
        println(json2)
        assertEquals(result1, json2)
    }

    @Test
    fun test_processGraphql_uuInclude() = suspendTest {
        val hasuraSubset = HasuraSubset()
        println("test_processGraphql_uuInclude input query: $tweetQueryWithUuInclude")
        val result = hasuraSubset.processGraphql(
            tweetQueryWithUuInclude,
            tweetGraphql,
            false,
            testResourceDir)
        println("test_processGraphql_uuInclude result: $result")
        assertEquals(tweetQueryExpaned, result)

    }

    @Test
    fun test_processGraphql_recursiveQuery() = suspendTest {
        val hasuraSubset = HasuraSubset()

        println("test_processGraphql_recursiveQuery input query: $tweetQueryWithUuIncludeRecursive")
        val result = hasuraSubset.processGraphql(
            tweetQueryWithUuIncludeRecursive,
            tweetGraphql,
            false,
            testResourceDir)
        println("test_processGraphql_recursiveQuery result: $result")
        assertEquals(tweetQueryWithUuIncludeRecursiveExpanded, result)
    }


}
