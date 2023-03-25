package dev.priyankvasa.sample.data.ktor.customfeatures

import dev.priyankvasa.sample.data.auth.AuthTokensProvider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.takeFrom
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.util.AttributeKey

internal class Authorization(config: Config) {
    class Config {
        lateinit var authTokensProvider: dev.priyankvasa.sample.data.auth.AuthTokensProvider
    }

    private val authTokensProvider = config.authTokensProvider

    suspend fun mayBeAddAuthBearerHeader(context: HttpRequestBuilder) {
        if (!authTokensProvider.isSecurePath(context.url.encodedPath)) return
        val authToken = authTokensProvider.getAccessToken() ?: return

        context.header(
            HttpHeaders.Authorization,
            HttpAuthHeader.Single("Bearer", authToken).render(),
        )
    }

    suspend fun validateAuthorization(scope: HttpClient, response: HttpResponse): HttpResponse =
        if (response.status == HttpStatusCode.Forbidden) {
            val refreshResponse = authTokensProvider.refreshTokens()

            if (refreshResponse.status.isSuccess()) {
                scope.request { takeFrom(response.request) }
            } else {
                refreshResponse
            }
        } else {
            response
        }

    companion object Plugin : HttpClientPlugin<Config, Authorization> {

        override val key: AttributeKey<Authorization> =
            AttributeKey("AuthorizationPlugin")

        override fun install(plugin: Authorization, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                plugin.mayBeAddAuthBearerHeader(context)
            }

            scope.receivePipeline.intercept(HttpReceivePipeline.Before) { response ->
                proceedWith(plugin.validateAuthorization(scope, response))
            }
        }

        override fun prepare(block: Config.() -> Unit): Authorization {
            val config = Config().apply(block)

            return Authorization(config)
        }
    }
}
