@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package dev.priyankvasa.sample.data.sse

import dev.priyankvasa.sample.data.core.model.UnknownApiException
import dev.priyankvasa.sample.data.core.util.runAppTaskCatching
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.ProxyConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.isSuccess
import io.ktor.utils.io.core.Closeable
import io.ktor.utils.io.errors.IOException
import io.ktor.utils.io.readUTF8Line
import kotlinx.atomicfu.AtomicLong
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlin.coroutines.cancellation.CancellationException
import kotlin.jvm.Volatile
import kotlin.math.min
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class EOFException(message: String) : Throwable(message)

// Returns 2**k, or Integer.MAX_VALUE if 2**k would overflow
fun pow2(k: Int): Int {
    return if (k < Int.SIZE_BITS - 1) 1 shl k else Int.MAX_VALUE
}

/**
 * Client for [Server-Sent Events](https://www.w3.org/TR/2015/REC-eventsource-20150203/)
 * aka EventSource
 */
internal class EventSource internal constructor(
    name: String?,
    private val logger: Antilog?,
    private val url: Url,
    headers: Headers,
    private val method: HttpMethod,
    private val requestTransformer: RequestTransformer?,
    @Volatile var reconnectTime: Duration,
    private val maxReconnectTime: Duration,
    private val backoffResetThreshold: Duration,
    lastEventId: String?,
    eventListener: EventListener?,
    connectionErrorHandler: ConnectionErrorHandler?,
    private val client: HttpClient,
    private val requestHandler: (suspend (suspend () -> HttpStatement) -> HttpStatement)?,
) : Closeable {
    private val headers: Headers = addDefaultHeaders(headers)

    private val streamExecutor =
        CoroutineScope(SupervisorJob() + CoroutineName("${name?.let { "$it-" } ?: ""}eventsource-stream"))

    /**
     * Returns the ID value, if any, of the last known event.
     *
     *
     * This can be set initially with [Builder.lastEventId], and is updated whenever an event
     * is received that has an ID. Whether event IDs are supported depends on the server; it may ignore this
     * value.
     *
     * @return the last known event ID, or null
     * @see Builder.lastEventId
     * @since 2.0.0
     */
    @Volatile
    var lastEventId: String? = lastEventId
        private set

    private val eventListener = AsyncEventListener(
        eventListener,
        logger,
        SupervisorJob() + CoroutineName("${name?.let { "$it-" } ?: ""}eventsource-events"),
    )

    private val connectionErrorHandler: ConnectionErrorHandler =
        connectionErrorHandler ?: DefaultConnectionErrorHandler

    private val eventSourceState: AtomicRef<EventSourceState> = atomic(EventSourceState.RAW)

    private var response: HttpResponse? = null

    private val jitter: Random = Random

    /**
     * Returns an enum indicating the current status of the connection.
     * @return a [EventSourceState] value
     */
    val state: EventSourceState
        get() = eventSourceState.value

    /**
     * Attempts to connect to the remote event source if not already connected. This method returns
     * immediately; the connection happens on a worker thread.
     */
    suspend fun start() {
        if (!eventSourceState.compareAndSet(EventSourceState.RAW, EventSourceState.CONNECTING)) {
            logger?.log(
                LogLevel.INFO,
                null,
                null,
                "Start method called on this already-started EventSource object. Doing nothing",
            )
            return
        }
        logger?.log(
            LogLevel.DEBUG,
            null,
            null,
            "readyState change: ${EventSourceState.RAW} -> ${EventSourceState.CONNECTING}",
        )
        logger?.log(LogLevel.INFO, null, null, "Starting EventSource client using URI: $url")

        startInternal()
    }

    /**
     * Drops the current stream connection (if any) and attempts to reconnect.
     *
     *
     * This method returns immediately after dropping the current connection; the reconnection happens on
     * a worker thread.
     *
     *
     * If a connection attempt is already in progress but has not yet connected, or if [.close] has
     * previously been called, this method has no effect. If [.start] has never been called, it is
     * the same as calling [.start].
     */
    suspend fun restart() {
        val previousState: EventSourceState =
            eventSourceState.getAndUpdate { t: EventSourceState -> if (t === EventSourceState.OPEN) EventSourceState.CLOSED else t }
        if (previousState === EventSourceState.OPEN) {
            closeCurrentStream(previousState)
        } else if (previousState === EventSourceState.RAW) {
            start()
        }
        // if already connecting or already shutdown or in the process of closing, do nothing
    }

    /**
     * Drops the current stream connection (if any) and permanently shuts down the EventSource.
     */
    override fun close() {
        val currentState: EventSourceState = eventSourceState.getAndSet(EventSourceState.SHUTDOWN)
        if (currentState === EventSourceState.SHUTDOWN) return

        logger?.log(
            LogLevel.DEBUG,
            null,
            null,
            "readyState change: ${currentState.name} -> ${EventSourceState.SHUTDOWN.name}",
        )

        closeCurrentStream(currentState)
    }

    private fun closeCurrentStream(previousState: EventSourceState) {
        if (closeReceiveChannel()) {
            logger?.log(LogLevel.DEBUG, null, null, "call cancelled")
        }

        eventListener.cancel("Closing the source")

        if (previousState === EventSourceState.OPEN) {
            eventListener.onClosed()
        }
    }

    private fun requestBuilder(): HttpRequestBuilder {
        val builder: HttpRequestBuilder = HttpRequestBuilder().apply {
            this.headers.appendAll(this@EventSource.headers)
            url(this@EventSource.url)
            method = this@EventSource.method
        }

        if (!lastEventId.isNullOrBlank()) {
            builder.header("Last-Event-ID", lastEventId)
        }

        return requestTransformer?.transformRequest(builder) ?: builder
    }

    private suspend fun startInternal() {
        val connectedTime: AtomicLong = atomic(0L)
        var reconnectAttempts = 0
        try {
            coroutineScope {
                while (state != EventSourceState.SHUTDOWN && isActive) {
                    if (reconnectAttempts == 0) {
                        reconnectAttempts++
                    } else {
                        reconnectAttempts =
                            maybeReconnectDelay(reconnectAttempts, connectedTime.value)
                    }
                    newConnectionAttempt(connectedTime)
                }
            }
        } catch (ignored: RuntimeException) {
            // COVERAGE: there is no way to simulate this condition in unit tests
            closeReceiveChannel()
            logger?.log(LogLevel.DEBUG, null, ignored, "Rejected execution exception ignored")
            // During shutdown, we tried to send a message to the event handler
            // Do not reconnect; the executor has been shut down
        }
    }

    private suspend fun maybeReconnectDelay(reconnectAttempts: Int, connectedTime: Long): Int {
        if (reconnectTime == Duration.ZERO || reconnectTime.isNegative()) {
            return reconnectAttempts
        }
        var counter = reconnectAttempts

        // Reset the backoff if we had a successful connection that stayed good for at least
        // backoffResetThresholdMs milliseconds.
        if (connectedTime > 0 &&
            Clock.System.now()
                .toEpochMilliseconds() - connectedTime >= backoffResetThreshold.inWholeMilliseconds
        ) {
            counter = 1
        }

        val sleepTime: Duration = backoffWithJitter(counter)

        logger?.log(
            LogLevel.INFO,
            null,
            null,
            "Waiting ${sleepTime.inWholeMilliseconds} milliseconds before reconnecting...",
        )

        delay(sleepTime)

        return ++counter
    }

    private suspend fun newConnectionAttempt(connectedTime: AtomicLong) {
        var errorHandlerAction: ConnectionErrorAction =
            ConnectionErrorAction.PROCEED
        val stateBeforeConnecting: EventSourceState =
            eventSourceState.getAndSet(EventSourceState.CONNECTING)
        logger?.log(
            LogLevel.DEBUG,
            null,
            null,
            "readyState change: ${stateBeforeConnecting.name} -> ${EventSourceState.CONNECTING.name}",
        )
        connectedTime.value = 0

        val call = requestHandler?.invoke { client.prepareRequest(requestBuilder()) }
            ?: client.prepareRequest(requestBuilder())

        try {
            call.execute { response: HttpResponse ->
                if (response.status.isSuccess()) {
                    connectedTime.value = Clock.System.now().toEpochMilliseconds()
                    this.response = response
                    handleSuccessfulResponse(response)
                } else {
                    logger?.log(LogLevel.DEBUG, null, null, "Unsuccessful response: $response")

                    val errorMessage = runAppTaskCatching { response.bodyAsText() }
                        .getOrNull()
                        ?: "Failed to connect!"

                    errorHandlerAction =
                        dispatchError(UnknownApiException(response.status.value, errorMessage))
                }
            }

            // If handleSuccessfulResponse returned without throwing an exception, it means the server
            // ended the stream. We don't call the handler's onError() method in this case; but we will
            // call the ConnectionErrorHandler with an EOFException, in case it wants to do something
            // special in this scenario (like choose not to retry the connection). However, first we
            // should check the state in case we've been deliberately closed from elsewhere.
            if (state !== EventSourceState.SHUTDOWN && state !== EventSourceState.CLOSED) {
                logger?.log(LogLevel.WARNING, null, null, "Connection unexpectedly closed")
                errorHandlerAction =
                    connectionErrorHandler(EOFException("Server closed the stream"))
            }
        } catch (e: IOException) {
            if (state !== EventSourceState.SHUTDOWN && state !== EventSourceState.CLOSED) {
                logger?.log(LogLevel.DEBUG, null, e, "Connection problem")
                errorHandlerAction = dispatchError(e)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger?.log(LogLevel.ERROR, null, e, null)
            throw e
        } finally {
            if (errorHandlerAction == ConnectionErrorAction.SHUTDOWN) {
                logger?.log(
                    LogLevel.INFO,
                    null,
                    null,
                    "Connection has been explicitly shut down by error handler",
                )
                close()
            } else {
                val wasOpen: Boolean =
                    eventSourceState.compareAndSet(EventSourceState.OPEN, EventSourceState.CLOSED)
                val wasConnecting: Boolean =
                    eventSourceState.compareAndSet(
                        EventSourceState.CONNECTING,
                        EventSourceState.CLOSED,
                    )
                if (wasOpen) {
                    logger?.log(
                        LogLevel.DEBUG,
                        null,
                        null,
                        "readyState change: ${EventSourceState.OPEN.name} -> ${EventSourceState.CLOSED.name}",
                    )
                    eventListener.onClosed()
                } else if (wasConnecting) {
                    logger?.log(
                        LogLevel.DEBUG,
                        null,
                        null,
                        "readyState change: ${EventSourceState.CONNECTING.name} -> ${EventSourceState.CLOSED.name}",
                    )
                }
            }
        }
    }

    // Read the response body as an SSE stream and dispatch each received event to the EventHandler.
    // This function exits in one of two ways:
    // 1. A normal return - this means the response simply ended.
    // 2. Throwing an IOException - there was an unexpected connection failure.
    @Throws(CancellationException::class, IOException::class)
    private suspend fun handleSuccessfulResponse(response: HttpResponse) {
        val connectionHandler: ConnectionHandler = object : ConnectionHandler {
            override fun setReconnectionTime(reconnectionTime: Duration) {
                this@EventSource.setReconnectionTime(reconnectionTime)
            }

            override fun setLastEventId(lastEventId: String?) {
                this@EventSource.setLastEventId(lastEventId)
            }
        }

        val previousState: EventSourceState = eventSourceState.getAndSet(EventSourceState.OPEN)

        if (previousState !== EventSourceState.CONNECTING) {
            // COVERAGE: there is no way to simulate this condition in unit tests
            logger?.log(
                LogLevel.WARNING,
                null,
                null,
                "Unexpected readyState change: ${previousState.name} -> ${EventSourceState.OPEN.name}",
            )
        } else {
            logger?.log(
                LogLevel.DEBUG,
                null,
                null,
                "readyState change: ${previousState.name} -> ${EventSourceState.OPEN.name}",
            )
        }
        logger?.log(LogLevel.INFO, null, null, "Connected to EventSource stream.")
        eventListener.onOpen()

        coroutineScope {
            runAppTaskCatching {
                // Response is not downloaded here.
                val receiveChannel = response.bodyAsChannel()

                val parser = EventParser(url, eventListener, connectionHandler, logger)

                while (isActive &&
                    state !== EventSourceState.CLOSED &&
                    state !== EventSourceState.SHUTDOWN &&
                    !receiveChannel.isClosedForRead
                ) {
                    receiveChannel.readUTF8Line()?.let { line ->
                        parser.line(line)
                    }
                }

                closeReceiveChannel()
            }
                .onFailure { t ->
                    logger?.log(LogLevel.ERROR, null, t, null)
                    // `t` should not be a EOFException because bufferedSource.exhausted() should have returned true,
                    // but if it does happen, we'll treat it the same as a regular end of stream.
                    if (t !is EOFException) throw t
                }
        }
    }

    private fun closeReceiveChannel(): Boolean {
        val channel = response
        response = null
        return channel?.cancel("Closing receive channel!") != null
    }

    private fun dispatchError(exception: Exception): ConnectionErrorAction {
        val action: ConnectionErrorAction = connectionErrorHandler(exception)
        if (action !== ConnectionErrorAction.SHUTDOWN) {
            eventListener.onError(exception)
        }
        return action
    }

    private fun backoffWithJitter(reconnectAttempts: Int): Duration {
        val maxTimeLong: Long = min(
            maxReconnectTime.inWholeMilliseconds,
            reconnectTime.inWholeMilliseconds * pow2(reconnectAttempts),
        )
        // 2^31 milliseconds is much longer than any reconnect time we would reasonably want to use, so we can pin this to int
        val maxTimeInt = if (maxTimeLong > Int.MAX_VALUE) Int.MAX_VALUE else maxTimeLong.toInt()
        return (maxTimeInt / 2 + jitter.nextInt(maxTimeInt) / 2).milliseconds
    }

    // setReconnectionTime and setLastEventId are used only by our internal ConnectionHandler, in response
    // to stream events. From an application's point of view, these properties can only be set at
    // configuration time via the builder.
    private fun setReconnectionTime(reconnectionTime: Duration) {
        reconnectTime = reconnectionTime
    }

    private fun setLastEventId(lastEventId: String?) {
        this.lastEventId = lastEventId
    }

    /**
     * Returns the current stream endpoint as an OkHttp HttpUrl.
     *
     * @return the endpoint URL
     * @since 1.9.0
     * @see .getUri
     */
    val httpUrl: Url get() = URLBuilder(url).build()

    /**
     * Interface for an object that can modify the network request that the EventSource will make.
     * Use this in conjunction with [EventSource.Builder.requestTransformer]
     * if you need to set request properties other than the ones that are already supported by the builder (or if,
     * for whatever reason, you need to determine the request properties dynamically rather than setting them
     * to fixed values initially). For example:
     * <pre>`
     * public class RequestTagger implements EventSource.RequestTransformer {
     * public Request transformRequest(Request input) {
     * return input.newBuilder().tag("hello").build();
     * }
     * }
     *
     * EventSource es = new EventSource.Builder(handler, uri).requestTransformer(new RequestTagger()).build();
     `</pre> *
     *
     * @since 1.9.0
     */
    interface RequestTransformer {
        /**
         * Returns a request that is either the same as the input request or based on it. When
         * this method is called, EventSource has already set all of its standard properties on
         * the request.
         *
         * @param input the original request
         * @return the request that will be used
         */
        fun transformRequest(input: HttpRequestBuilder): HttpRequestBuilder
    }

    /**
     * Builder for [EventSource].
     */
    class Builder(private val url: Url) {
        constructor(urlBuilder: URLBuilder) : this(urlBuilder.build())

        private var name: String? = null
        private var reconnectTime: Duration = DEFAULT_RECONNECT_TIME
        private var maxReconnectTime: Duration = DEFAULT_MAX_RECONNECT_TIME
        private var backoffResetThreshold: Duration = DEFAULT_BACKOFF_RESET_THRESHOLD
        private var lastEventId: String? = null
        private var connectionErrorHandler: ConnectionErrorHandler? = DefaultConnectionErrorHandler

        private var headers: Headers = Headers.Empty
        private var method: HttpMethod = HttpMethod.Get

        private var requestTransformer: RequestTransformer? = null

        private var logger: Antilog? = null
        private var loggerBaseName: String? = null

        private var httpClient: HttpClient? = null

        private var connectTimeout: Duration = DEFAULT_CONNECT_TIMEOUT
        private var requestTimeout: Duration = DEFAULT_REQUEST_TIMEOUT
        private var proxy: ProxyConfig? = null

        private var eventListener: EventListener? = null

        private var requestHandler: (suspend (suspend () -> HttpStatement) -> HttpStatement)? =
            null

        fun eventListener(listener: EventListener): Builder {
            this.eventListener = listener
            return this
        }

        fun requestHandler(handler: suspend (suspend () -> HttpStatement) -> HttpStatement): Builder {
            this.requestHandler = handler
            return this
        }

        /**
         * Set the HTTP method used for this EventSource client to use for requests to establish the EventSource.
         *
         *
         * Defaults to "GET".
         *
         * @param method the HTTP method name; if null or empty, "GET" is used as the default
         * @return the builder
         */
        fun method(method: String): Builder {
            method.takeIf { it.isNotBlank() }
                ?.uppercase()
                ?.let { runAppTaskCatching { this.method = HttpMethod.parse(it) } }

            return this
        }

        /**
         * Specifies an object that will be used to customize outgoing requests. See [RequestTransformer] for details.
         *
         * @param requestTransformer the transformer object
         * @return the builder
         *
         * @since 1.9.0
         */
        fun requestTransformer(requestTransformer: RequestTransformer): Builder {
            this.requestTransformer = requestTransformer
            return this
        }

        /**
         * Set the name for this EventSource client to be used when naming the logger and threadpools. This is mainly useful when
         * multiple EventSource clients exist within the same process.
         *
         *
         * The name only affects logging when using the default SLF4J integration; if you have specified a custom
         * [.logger], the name will not be included in log messages unless your logger implementation adds it.
         *
         * @param name the name (without any whitespaces)
         * @return the builder
         */
        fun name(name: String): Builder {
            this.name = name
            return this
        }

        /**
         * Sets the ID value of the last event received.
         *
         * This will be sent to the remote server on the initial connection request, allowing the server to
         * skip past previously sent events if it supports this behavior. Once the connection is established,
         * this value will be updated whenever an event is received that has an ID. Whether event IDs are
         * supported depends on the server; it may ignore this value.
         *
         * @param lastEventId the last event identifier
         * @return the builder
         * @since 2.0.0
         */
        fun lastEventId(lastEventId: String): Builder {
            this.lastEventId = lastEventId
            return this
        }

        /**
         * Sets the minimum delay between connection attempts. The actual delay may be slightly less or
         * greater, since there is a random jitter. When there is a connection failure, the delay will
         * start at this value and will increase exponentially up to the [.maxReconnectTime]
         * value with each subsequent failure, unless it is reset as described in
         * [Builder.backoffResetThreshold].
         *
         * @param reconnectTime the minimum delay; null to use the default
         * @return the builder
         * @see EventSource.DEFAULT_RECONNECT_TIME
         */
        fun reconnectTime(reconnectTime: Duration): Builder {
            this.reconnectTime = reconnectTime
            return this
        }

        /**
         * Sets the maximum delay between connection attempts. See [.reconnectTime].
         * The default value is 30 seconds.
         *
         * @param maxReconnectTime the maximum delay; null to use the default
         * @return the builder
         * @see EventSource.DEFAULT_MAX_RECONNECT_TIME
         */
        fun maxReconnectTime(maxReconnectTime: Duration): Builder {
            this.maxReconnectTime = maxReconnectTime
            return this
        }

        /**
         * Sets the minimum amount of time that a connection must stay open before the EventSource resets its
         * backoff delay. If a connection fails before the threshold has elapsed, the delay before reconnecting
         * will be greater than the last delay; if it fails after the threshold, the delay will start over at
         * the initial minimum value. This prevents long delays from occurring on connections that are only
         * rarely restarted.
         *
         * @param backoffResetThreshold the minimum time that a connection must stay open to avoid resetting
         * the delay; null to use the default
         * @return the builder
         * @see EventSource.DEFAULT_BACKOFF_RESET_THRESHOLD
         */
        fun backoffResetThreshold(backoffResetThreshold: Duration): Builder {
            this.backoffResetThreshold = backoffResetThreshold
            return this
        }

        /**
         * Set the headers to be sent when establishing the EventSource connection.
         *
         * @param headers headers to be sent with the EventSource request
         * @return the builder
         */
        fun headers(headers: Headers): Builder {
            this.headers = headers
            return this
        }

        /**
         * Set the SOCKS proxy address to be used to make the EventSource connection
         *
         * @param proxyHost the proxy hostname
         * @param proxyPort the proxy port
         * @return the builder
         */
        fun socksProxy(proxyHost: String, proxyPort: Int): Builder {
            this.proxy = ProxyBuilder.socks(proxyHost, proxyPort)
            return this
        }

        /**
         * Set the HTTP proxy url to be used to make the EventSource connection
         *
         * @param url the proxy [Url]
         * @return the builder
         */
        fun httpProxy(url: Url): Builder {
            this.proxy = ProxyBuilder.http(url)
            return this
        }

        /**
         * Set the [ProxyConfig] to be used to make the EventSource connection.
         *
         * @param proxy the proxy
         * @return the builder
         */
        fun proxy(proxy: ProxyConfig): Builder {
            this.proxy = proxy
            return this
        }

        /**
         * Set a custom HTTP client that will be used to make the EventSource connection.
         *
         * Timeouts and proxy config will always be overridden with either the ones set using
         * builder method or defaults; the ones in [client] will be ignored
         *
         * If you're setting this along with [proxy], you should do this first to avoid overwriting values.
         *
         * @param client the HTTP client
         * @return the builder
         */
        fun clientConfig(client: HttpClient): Builder {
            httpClient = client
            return this
        }

        /**
         * Sets the [ConnectionErrorHandler] that should process connection errors.
         *
         * @param handler the error handler
         * @return the builder
         */
        fun connectionErrorHandler(handler: ConnectionErrorHandler): Builder {
            connectionErrorHandler = handler
            return this
        }

        /**
         * Specifies a custom logger to receive EventSource logging.
         *
         *
         * If you do not provide a logger, the default is to send log output to SLF4J.
         *
         * @param logger a [Antilog] implementation, or null to use the default (SLF4J)
         * @return the builder
         * @since 2.3.0
         */
        fun logger(logger: Antilog): Builder {
            this.logger = logger
            return this
        }

        /**
         * Sets the connection timeout.
         *
         * @param connectTimeout the connection timeout
         * @return the builder
         * @see EventSource.DEFAULT_CONNECT_TIMEOUT
         */
        fun connectTimeout(connectTimeout: Duration): Builder {
            this.connectTimeout = connectTimeout
            return this
        }

        /**
         * Sets the request timeout.
         *
         * @param requestTimeout the write timeout
         * @return the builder
         * @see EventSource.DEFAULT_REQUEST_TIMEOUT
         */
        fun requestTimeout(requestTimeout: Duration): Builder {
            this.requestTimeout = requestTimeout
            return this
        }

        /**
         * Specifies the base logger name to use for SLF4J logging.
         *
         *
         * The default is `com.launchdarkly.eventsource.EventSource`, plus any name suffix specified
         * by [.name]. If you instead use [.logger] to specify some other log
         * destination rather than SLF4J, this name is unused.
         *
         * @param loggerBaseName the SLF4J logger name, or null to use the default
         * @return the builder
         * @since 2.3.0
         */
        fun loggerBaseName(loggerBaseName: String): Builder {
            this.loggerBaseName = loggerBaseName
            return this
        }

        /**
         * Constructs an [EventSource] using the builder's current properties.
         * @return the new EventSource instance
         */
        fun build(): EventSource {
            val clientConfig: HttpClientConfig<*>.() -> Unit = {
                if (this@Builder.proxy != null) {
                    engine {
                        this.proxy = this@Builder.proxy
                    }
                }

                install(HttpTimeout) {
                    this.requestTimeoutMillis = requestTimeout.inWholeMilliseconds
                    this.socketTimeoutMillis = requestTimeout.inWholeMilliseconds
                    this.connectTimeoutMillis = connectTimeout.inWholeMilliseconds
                }
            }

            val client = httpClient?.config(clientConfig) ?: HttpClient(clientConfig)

            return EventSource(
                name?.takeIf { it.isNotBlank() },
                logger,
                url,
                headers,
                method,
                requestTransformer,
                reconnectTime,
                maxReconnectTime,
                backoffResetThreshold,
                lastEventId,
                eventListener,
                connectionErrorHandler,
                client,
                requestHandler,
            )
        }
    }

    companion object {
        /**
         * The default value for [Builder.reconnectTime]: 1 second.
         */
        val DEFAULT_RECONNECT_TIME: Duration = 1.seconds

        /**
         * The default value for [Builder.maxReconnectTime]: 30 seconds.
         */
        val DEFAULT_MAX_RECONNECT_TIME: Duration = 30.seconds

        /**
         * The default value for [Builder.connectTimeout]: 30 seconds.
         */
        val DEFAULT_CONNECT_TIMEOUT: Duration = 30.seconds

        /**
         * The default value for [Builder.requestTimeout]: infinite (never timeout streaming requests).
         */
        val DEFAULT_REQUEST_TIMEOUT: Duration =
            HttpTimeout.INFINITE_TIMEOUT_MS.milliseconds

        /**
         * The default value for [Builder.backoffResetThreshold]: 60 seconds.
         */
        val DEFAULT_BACKOFF_RESET_THRESHOLD: Duration = 60.seconds
        private val defaultHeaders: Headers =
            Headers.build {
                append(HttpHeaders.Accept, ContentType.Text.EventStream.toString())
                append(HttpHeaders.CacheControl, CacheControl.NoCache(null).toString())
            }

        private fun addDefaultHeaders(custom: Headers): Headers {
            return Headers.build {
                appendAll(custom)
                appendMissing(defaultHeaders)
            }
        }
    }
}
