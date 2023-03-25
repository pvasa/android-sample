package dev.priyankvasa.sample.data.sse

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel
import io.ktor.http.Url
import kotlin.time.Duration.Companion.milliseconds

/**
 * Adapted from https://github.com/aslakhellesoy/eventsource-java/blob/master/src/main/java/com/github/eventsource/client/impl/EventStreamParser.java
 */
internal class EventParser(
    private val origin: Url,
    private val listener: EventListener,
    private val connectionHandler: ConnectionHandler,
    private val logger: Antilog?,
) {
    private var data = StringBuilder()
    private var lastEventId: String? = null
    private var eventName = DEFAULT_EVENT

    /**
     * Accepts a single line of input and updates the parser state. If this completes a valid event,
     * the event is sent to the [EventListener].
     * @param line an input line
     */
    fun line(line: String) {
        logger?.log(LogLevel.DEBUG, null, null, "Parsing line: $line")

        var colonIndex: Int

        when {
            line.trim { it <= ' ' }.isEmpty() -> {
                dispatchEvent()
            }
            line.startsWith(":") -> {
                processComment(line.substring(1).trim { it <= ' ' })
            }
            line.indexOf(":").also { colonIndex = it } != -1 -> {
                val field = line.substring(0, colonIndex)
                var value = line.substring(colonIndex + 1)
                if (value.isNotBlank() && value[0] == ' ') {
                    value = value.replaceFirst(" ".toRegex(), EMPTY_STRING)
                }
                processField(field, value)
            }
            else -> {
                processField(
                    line.trim { it <= ' ' },
                    EMPTY_STRING,
                ) // The spec doesn't say we need to trim the line, but I assume that's an oversight.
            }
        }
    }

    private fun processComment(comment: String) {
        try {
            listener.onComment(comment)
        } catch (e: Exception) {
            listener.onError(e)
        }
    }

    private fun processField(field: String, value: String) {
        if (DATA == field) {
            data.append(value).append("\n")
        } else if (ID == field) {
            lastEventId = value
        } else if (EVENT == field) {
            eventName = value
        } else if (RETRY == field && isNumber(value)) {
            connectionHandler.setReconnectionTime(value.toLong().milliseconds)
        }
    }

    private fun isNumber(value: String): Boolean {
        return DIGITS_ONLY.matches(value)
    }

    private fun dispatchEvent() {
        if (data.isEmpty()) return

        var dataString: String = data.toString()
        if (dataString.endsWith("\n")) {
            dataString = dataString.substring(0, dataString.length - 1)
        }
        val message = MessageEvent(eventName, dataString, lastEventId, origin)
        connectionHandler.setLastEventId(lastEventId)
        try {
            logger?.log(
                LogLevel.DEBUG,
                null,
                null,
                "Dispatching message: \"$eventName\", $message",
            )
            listener.onMessage(message)
        } catch (e: Exception) {
            logger?.log(LogLevel.WARNING, null, e, "Message handler threw an exception")
            logger?.log(LogLevel.DEBUG, null, null, "Stack trace: ${LazyStackTrace(e)}")
            listener.onError(e)
        }
        data = StringBuilder()
        eventName = DEFAULT_EVENT
    }

    companion object {
        private const val DATA = "data"
        private const val ID = "id"
        private const val EVENT = "event"
        private const val RETRY = "retry"
        private const val DEFAULT_EVENT = "message"
        private const val EMPTY_STRING = ""
        private val DIGITS_ONLY: Regex = Regex("^[\\d]+$")
    }
}
