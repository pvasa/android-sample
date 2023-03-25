package dev.priyankvasa.sample.data.core.remote

import dev.priyankvasa.sample.data.core.model.RemoteApiConfig
import dev.priyankvasa.sample.data.ktor.util.urlWithBase
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpPlainText
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.addDefaultResponseValidation
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.charsets.Charsets

internal expect fun createBaseHttpClient(): HttpClient

internal fun Logging.Config.defaultHttpClientLogging(config: RemoteApiConfig) {
    logger = object : Logger {
        override fun log(message: String) {
            Napier.i(message)
        }
    }

    level = with(config) {
        if (logInfo && logHeaders && logBody) {
            LogLevel.ALL
        } else if (logInfo && logHeaders) {
            LogLevel.HEADERS
        } else if (logInfo && logBody) {
            LogLevel.BODY
        } else if (logInfo) {
            LogLevel.INFO
        } else {
            LogLevel.NONE
        }
    }
}

internal fun HttpClient(config: RemoteApiConfig): HttpClient =
    createBaseHttpClient().config {
        // install(LogPipelinesPlugin)

        defaultRequest {
            urlWithBase(config.baseUrl)
        }

        install(Logging) {
            defaultHttpClientLogging(config)
        }

        install(HttpTimeout) {
            // timeout config
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }

        install(HttpPlainText) {
            // Allow to use `UTF_8`.
            register(Charsets.UTF_8)

            // Allow to use `ISO_8859_1` with quality 0.1.
            register(Charsets.ISO_8859_1, quality = 0.1f)

            // Specify Charset to send request(if no charset in request headers).
            sendCharset = Charsets.UTF_8

            // Specify Charset to receive response(if no charset in response headers).
            responseCharsetFallback = Charsets.UTF_8
        }

        addDefaultResponseValidation()
        HttpResponseValidator {
            handleResponseExceptionWithRequest { cause, _ ->
                Napier.w("", cause)
            }
        }

        install(ContentNegotiation) {
            json(config.json)
        }
    }
