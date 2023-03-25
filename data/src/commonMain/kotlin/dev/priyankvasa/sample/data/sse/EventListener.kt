package dev.priyankvasa.sample.data.sse

/**
 * Interface for an object that will receive SSE events.
 */
internal interface EventListener {
    /**
     * EventSource calls this method when the stream connection has been opened.
     * @throws Exception throwing an exception here will cause it to be logged and also sent to [.onError]
     */
    @Throws(Exception::class)
    fun onOpen()

    /**
     * EventSource calls this method when the stream connection has been closed.
     *
     *
     * This method is *not* called if the connection was closed due to a [ConnectionErrorHandler]
     * returning [ConnectionErrorHandler.ConnectionErrorAction.SHUTDOWN]; EventSource assumes that if you registered
     * such a handler and made it return that value, then you already know that the connection is being closed.
     *
     *
     * There is a known issue where `onClosed()` may or may not be called if the stream has been
     * permanently closed by calling `close()`.
     *
     * @throws Exception throwing an exception here will cause it to be logged and also sent to [.onError]
     */
    @Throws(Exception::class)
    fun onClosed()

    /**
     * EventSource calls this method when it has received a new event from the stream.
     * @param messageEvent a [MessageEvent] object containing all the event properties
     * @throws Exception throwing an exception here will cause it to be logged and also sent to [.onError]
     */
    @Throws(Exception::class)
    fun onMessage(messageEvent: MessageEvent)

    /**
     * EventSource calls this method when it has received a comment line from the stream (any line starting with a colon).
     * @param comment the comment line
     * @throws Exception throwing an exception here will cause it to be logged and also sent to [.onError]
     */
    @Throws(Exception::class)
    fun onComment(comment: String)

    /**
     * This method will be called for all exceptions that occur on the socket connection (including
     * an [RuntimeException] if the server returns an unexpected HTTP status),
     * but only after the [ConnectionErrorHandler] (if any) has processed it.  If you need to
     * do anything that affects the state of the connection, use [ConnectionErrorHandler].
     *
     *
     * This method is *not* called if the error was already passed to a [ConnectionErrorHandler]
     * which returned [ConnectionErrorHandler.ConnectionErrorAction.SHUTDOWN]; EventSource assumes that if you registered
     * such a handler and made it return that value, then you do not want to handle the same error twice.
     *
     * @param t  a `Throwable` object
     */
    fun onError(t: Throwable)
}
