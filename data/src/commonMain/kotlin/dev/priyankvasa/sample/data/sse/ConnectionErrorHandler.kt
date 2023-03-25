package dev.priyankvasa.sample.data.sse

/**
 * A lambda that will be notified when EventSource encounters a socket connection
 * error or receives an error response. This is different from [EventListener.onError]
 * in two ways:
 *
 *  1.  It has the ability to tell EventSource to shut down instead of reconnecting.
 *  1.  If the server simply ends the stream, the `ConnectionErrorHandler` is called with
 * an [EOFException]; `onError` is not called in this case.
 *
 * It is called synchronously for all exceptions that occur on the socket connection
 * (including an [RuntimeException] if the server returns an unexpected HTTP
 * status, or [EOFException] if the streaming response has ended).
 *
 * It must not take any direct action to affect the state of the connection, nor do any I/O of
 * its own, but it can return [ConnectionErrorAction.SHUTDOWN] to cause the connection to close.
 *
 * @return an [ConnectionErrorAction] constant
 */
internal typealias ConnectionErrorHandler = (Throwable) -> ConnectionErrorAction

/**
 * Default handler that does nothing.
 */
internal val DefaultConnectionErrorHandler: ConnectionErrorHandler = { _ ->
    ConnectionErrorAction.PROCEED
}

/**
 * Return values of [ConnectionErrorHandler.onConnectionError] indicating what
 * action the [EventSource] should take after an error.
 */
internal enum class ConnectionErrorAction {
    /**
     * Specifies that the error should be logged normally and dispatched to the [EventListener].
     * Connection retrying will proceed normally if appropriate.
     */
    PROCEED,

    /**
     * Specifies that the connection should be immediately shut down and not retried.  The error
     * will not be dispatched to the [EventListener].
     */
    SHUTDOWN,
}
