package hu.sztaki.dsd

import io.ktor.client.*
import kotlinx.serialization.json.Json

/**
 * Hasura subsetting API.
 */
class HasuraSubset {

    data class Result(
        val fromServerResult: String? = null,
        val fromServerError: String? = null,
        val toServerResult: String? = null,
        val toServerError: String? = null
    )

    fun jsonToUpsert(jsonString: String) : String
    {
        val json = Json.parseToJsonElement(jsonString)
        println("********** jsonToUpsert called, OK *************")
        return "TODO"
    }

    fun copy(graphql: String, fromServer: HasuraServer, toServer: HasuraServer): Result
    {
        TODO()
    }

}