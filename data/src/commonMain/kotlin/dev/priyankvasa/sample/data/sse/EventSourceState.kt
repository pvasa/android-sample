package dev.priyankvasa.sample.data.sse

/**
 * Enum values that can be returned by [EventSource.state].
 */
internal enum class EventSourceState {
    /**
     * The EventSource's [EventSource.start] method has not yet been called.
     */
    RAW,

    /**
     * The EventSource is attempting to make a connection.
     */
    CONNECTING,

    /**
     * The connection is active and the EventSource is listening for events.
     */
    OPEN,

    /**
     * The connection has been closed or has failed, and the EventSource will attempt to reconnect.
     */
    CLOSED,

    /**
     * The connection has been permanently closed and will not reconnect.
     */
    SHUTDOWN,
    ;

    override fun toString(): String = name
}
