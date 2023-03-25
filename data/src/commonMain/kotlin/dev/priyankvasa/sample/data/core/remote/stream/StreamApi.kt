package dev.priyankvasa.sample.data.core.remote.stream

import dev.priyankvasa.sample.data.core.util.appendAll
import dev.priyankvasa.sample.data.sse.ConnectionErrorAction
import dev.priyankvasa.sample.data.sse.EOFException
import dev.priyankvasa.sample.data.sse.EventListener
import dev.priyankvasa.sample.data.sse.EventSource
import dev.priyankvasa.sample.data.sse.MessageEvent
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

internal interface StreamApi {
    fun sseMessages(
        encodedPath: String,
        queryParameters: Map<String, String>? = null,
    ): Flow<StreamEvent>
}

internal class StreamApiDefault(private val httpClient: HttpClient) : StreamApi {
    override fun sseMessages(
        encodedPath: String,
        queryParameters: Map<String, String>?,
    ): Flow<StreamEvent> = callbackFlow {
        val eventSource = eventSource(httpClient, encodedPath, queryParameters) {
            trySend(it.toStreamEvent())
        }

        eventSource.start()

        awaitClose { eventSource.close() }
    }
        .buffer(Channel.UNLIMITED)

    private fun eventSource(
        httpClient: HttpClient,
        encodedPath: String,
        queryParameters: Map<String, Any>? = null,
        onMessage: (MessageEvent) -> Unit,
    ): EventSource =
        EventSource.Builder(
            URLBuilder(
                parameters = Parameters.build {
                    queryParameters?.let(::appendAll)
                },
            ).apply {
                this.encodedPath = encodedPath
            },
        )
            .clientConfig(httpClient)
            .eventListener(object : EventListener {
                override fun onOpen() {
                    Napier.i("Stream opened")
                }

                override fun onClosed() {
                    Napier.i("Stream closed")
                }

                override fun onMessage(messageEvent: MessageEvent) {
                    Napier.i("Stream event \"${messageEvent.event}\" received: $messageEvent")
                    onMessage(messageEvent)
                }

                override fun onComment(comment: String) {
                    Napier.i("Stream comment: $comment")
                }

                override fun onError(t: Throwable) {
                    Napier.w("Stream error", t)
                }
            })
            .connectionErrorHandler {
                if (it is EOFException) {
                    ConnectionErrorAction.SHUTDOWN
                } else {
                    ConnectionErrorAction.PROCEED
                }
            }
            .build()
}
