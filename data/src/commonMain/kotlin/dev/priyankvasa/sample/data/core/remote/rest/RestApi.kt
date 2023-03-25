package dev.priyankvasa.sample.data.core.remote.rest

import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.prepareDelete
import io.ktor.client.request.prepareGet
import io.ktor.client.request.preparePost
import io.ktor.client.request.preparePut
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpStatement
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal interface RestApi {
    suspend fun get(
        encodedPath: String,
        queryParameters: Map<String, Any?>? = null,
    ): HttpStatement

    suspend fun post(
        encodedPath: String,
        body: Any = Unit,
        queryParameters: Map<String, Any?>? = null,
    ): HttpStatement

    suspend fun put(
        encodedPath: String,
        body: Any = Unit,
        queryParameters: Map<String, Any?>? = null,
    ): HttpStatement

    suspend fun delete(
        encodedPath: String,
        queryParameters: Map<String, Any?>? = null,
    ): HttpStatement
}

internal fun RestApi(
    httpClient: HttpClient,
): RestApi = object : RestApi {
    override suspend fun get(
        encodedPath: String,
        queryParameters: Map<String, Any?>?,
    ): HttpStatement = httpClient.prepareGet {
        url(path = encodedPath)
        queryParameters?.forEach { (key, value) ->
            parameter(key, value)
        }
    }

    override suspend fun post(
        encodedPath: String,
        body: Any,
        queryParameters: Map<String, Any?>?,
    ): HttpStatement =
        httpClient.preparePost {
            contentType(ContentType.Application.Json)
            url(path = encodedPath)
            setBody(body)
            queryParameters?.forEach { (key, value) ->
                parameter(key, value)
            }
        }

    override suspend fun put(
        encodedPath: String,
        body: Any,
        queryParameters: Map<String, Any?>?,
    ): HttpStatement =
        httpClient.preparePut {
            contentType(ContentType.Application.Json)
            url(path = encodedPath)
            setBody(body)
            queryParameters?.forEach { (key, value) ->
                parameter(key, value)
            }
        }

    override suspend fun delete(
        encodedPath: String,
        queryParameters: Map<String, Any?>?,
    ): HttpStatement =
        httpClient.prepareDelete {
            url(path = encodedPath)
            queryParameters?.forEach { (key, value) ->
                parameter(key, value)
            }
        }
}
