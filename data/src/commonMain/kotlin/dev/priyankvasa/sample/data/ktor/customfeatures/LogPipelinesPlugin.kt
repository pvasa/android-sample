package dev.priyankvasa.sample.data.ktor.customfeatures

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.util.AttributeKey

internal class LogPipelinesPlugin {
    companion object Plugin : HttpClientPlugin<Unit, LogPipelinesPlugin> {
        override val key: AttributeKey<LogPipelinesPlugin> =
            AttributeKey("LogPipelinesPlugin")

        override fun install(plugin: LogPipelinesPlugin, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Before) { httpResponse ->
                Napier.i("PIPELINE-request PHASE-Before: $httpResponse")
                proceed()
            }

            scope.requestPipeline.intercept(HttpRequestPipeline.Render) { httpResponse ->
                Napier.i("PIPELINE-request PHASE-Render: $httpResponse")
                proceed()
            }

            scope.requestPipeline.intercept(HttpRequestPipeline.Send) { httpResponse ->
                Napier.i("PIPELINE-request PHASE-Send: $httpResponse")
                proceed()
            }

            scope.requestPipeline.intercept(HttpRequestPipeline.Transform) { httpResponse ->
                Napier.i("PIPELINE-request PHASE-Transform: $httpResponse")
                proceed()
            }

            scope.sendPipeline.intercept(HttpSendPipeline.Before) { httpResponse ->
                Napier.i("PIPELINE-send PHASE-Before: $httpResponse")
                proceed()
            }

            scope.sendPipeline.intercept(HttpSendPipeline.Receive) { httpClientCall ->
                Napier.i("PIPELINE-send PHASE-Receive: $httpClientCall")
                proceed()
            }

            scope.receivePipeline.intercept(HttpReceivePipeline.Before) { httpResponse ->
                Napier.i("PIPELINE-receive PHASE-Before: $httpResponse")
                proceed()
            }

            scope.receivePipeline.intercept(HttpReceivePipeline.After) { httpResponse ->
                Napier.i("PIPELINE-receive PHASE-After: $httpResponse")
                proceed()
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.Receive) { container ->
                Napier.i("PIPELINE-response PHASE-Receive: $container")
                proceed()
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.Parse) { container ->
                Napier.i("PIPELINE-response PHASE-Parse: $container")
                proceed()
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.After) { container ->
                Napier.i("PIPELINE-response PHASE-After: $container")
                proceed()
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { container ->
                Napier.i("PIPELINE-response PHASE-Transform: $container")
                proceed()
            }
        }

        override fun prepare(block: Unit.() -> Unit): LogPipelinesPlugin =
            LogPipelinesPlugin()
    }
}
