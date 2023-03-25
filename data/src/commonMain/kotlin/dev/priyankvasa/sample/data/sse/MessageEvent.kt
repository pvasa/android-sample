package dev.priyankvasa.sample.data.sse

import io.ktor.http.Url

/**
 * Event information that is passed to [EventListener.onMessage].
 */
internal data class MessageEvent internal constructor(
    /**
     * the event name, from the `event:` line in the stream
     */
    val event: String,
    /**
     * Returns the event data, if any.
     * @return the data string or null
     */
    val data: String,
    /**
     * Returns ID of the last event, if any.
     * @return the event ID or null
     */
    val lastEventId: String? = null,
    val origin: Url,
)
