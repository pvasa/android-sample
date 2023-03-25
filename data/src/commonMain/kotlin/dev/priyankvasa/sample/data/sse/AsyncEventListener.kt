package dev.priyankvasa.sample.data.sse

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Adapted from https://github.com/aslakhellesoy/eventsource-java/blob/master/src/main/java/com/github/eventsource/client/impl/AsyncEventSourceHandler.java
 *
 * We use this in conjunction with a *single-threaded* executor to ensure that messages are handled
 * on a worker thread in the same order that they were received.
 *
 * This class guarantees that runtime exceptions are never thrown back to the EventSource.
 */
internal class AsyncEventListener(
    private val eventSourceListener: EventListener?,
    private val logger: Antilog?,
    context: CoroutineContext = EmptyCoroutineContext,
) : EventListener, CoroutineScope by CoroutineScope(context) {

    override fun onOpen() {
        launch {
            try {
                eventSourceListener?.onOpen()
            } catch (e: Exception) {
                handleUnexpectedError(e)
            }
        }
    }

    override fun onClosed() {
        launch {
            try {
                eventSourceListener?.onClosed()
            } catch (e: Exception) {
                handleUnexpectedError(e)
            }
        }
    }

    override fun onComment(comment: String) {
        launch {
            try {
                eventSourceListener?.onComment(comment)
            } catch (e: Exception) {
                handleUnexpectedError(e)
            }
        }
    }

    override fun onMessage(messageEvent: MessageEvent) {
        launch {
            try {
                eventSourceListener?.onMessage(messageEvent)
            } catch (e: Exception) {
                handleUnexpectedError(e)
            }
        }
    }

    override fun onError(t: Throwable) {
        launch { onErrorInternal(t) }
    }

    private fun handleUnexpectedError(error: Throwable) {
        logger?.log(LogLevel.WARNING, null, error, "Caught unexpected error from EventHandler")
        logger?.log(LogLevel.DEBUG, null, null, "Stack trace: ${LazyStackTrace(error)}")
        onErrorInternal(error)
    }

    private fun onErrorInternal(error: Throwable) {
        try {
            eventSourceListener?.onError(error)
        } catch (errorFromErrorHandler: Throwable) {
            logger?.log(
                LogLevel.WARNING,
                null,
                errorFromErrorHandler,
                "Caught unexpected error from EventHandler.onError()",
            )
            logger?.log(LogLevel.DEBUG, null, null, "Stack trace: ${LazyStackTrace(error)}")
        }
    }
}
