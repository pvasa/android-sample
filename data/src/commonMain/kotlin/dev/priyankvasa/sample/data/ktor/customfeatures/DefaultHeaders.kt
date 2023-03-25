package dev.priyankvasa.sample.data.ktor.customfeatures

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.util.AttributeKey

internal interface Config {
    val headers: Map<String, Any?>

    fun header(key: String, value: Any?)
}

internal class DefaultHeaders(config: Config) : Config by config {
    // Feature configuration class
    class ConfigImpl : Config {
        private val _headers = mutableMapOf<String, Any?>()
        override val headers get() = _headers.toMap()

        override fun header(key: String, value: Any?) {
            _headers[key] = value
        }
    }

    /**
     * Installable feature for [DefaultHeaders].
     */
    companion object Plugin : HttpClientPlugin<Config, DefaultHeaders> {
        override val key = AttributeKey<DefaultHeaders>("DefaultHeaders")

        override fun install(plugin: DefaultHeaders, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                plugin.headers.forEach { (key, value) ->
                    context.header(key, value)
                }
            }
        }

        override fun prepare(block: Config.() -> Unit): DefaultHeaders =
            DefaultHeaders(ConfigImpl().apply(block))
    }
}
