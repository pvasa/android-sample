package dev.priyankvasa.sample.data.core.remote.stream

import dev.priyankvasa.sample.data.sse.MessageEvent

data class StreamEvent(
    val eventId: String?,
    val eventName: String,
    val data: String,
)

internal fun MessageEvent.toStreamEvent(): StreamEvent =
    StreamEvent(
        eventId = lastEventId,
        eventName = event,
        data = data,
    )
