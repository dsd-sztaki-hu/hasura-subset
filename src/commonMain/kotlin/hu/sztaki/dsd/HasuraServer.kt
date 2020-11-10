package hu.sztaki.dsd

import io.ktor.client.*

class HasuraServer(
    val client: HttpClient,
    val url: String,
    val adminSecret: String
)
{
}