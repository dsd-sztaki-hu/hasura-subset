package hu.sztaki.dsd.hasura.subset

import io.ktor.client.*

class HasuraServer(
    val client: HttpClient,
    val url: String,
    val adminSecret: String
)
{
}