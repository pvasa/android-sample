package dev.priyankvasa.sample.data.sse

import kotlin.time.Duration

internal interface ConnectionHandler {
    fun setReconnectionTime(reconnectionTime: Duration)
    fun setLastEventId(lastEventId: String?)
}
