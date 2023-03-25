package dev.priyankvasa.sample.data.sse

/**
 * This class wraps a exception so that we can log its stacktrace in a debug message without
 * actually computing the stacktrace unless the logger has enabled debug output.
 *
 * We should only use this for cases where the stacktrace may actually be informative, such
 * as an unchecked exception thrown from an application's message handler. For I/O exceptions
 * where the source of the exception can be clearly indicated by the log message, we should
 * not log stacktraces.
 */
internal class LazyStackTrace(private val throwable: Throwable) : Throwable() {
    override fun toString(): String = throwable.stackTraceToString()
}
