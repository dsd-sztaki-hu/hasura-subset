package hu.sztaki.dsd

import io.ktor.client.*

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

    fun jsonToUpsert(json: String) : String
    {
        TODO()
    }

    fun copy(graphql: String, fromServer: HasuraServer, toServer: HasuraServer): Result
    {
        TODO()
    }

}