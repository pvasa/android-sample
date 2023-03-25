package dev.priyankvasa.sample.android.ui.util

import dev.priyankvasa.sample.android.ui.BuildConfig
import dev.priyankvasa.sample.data.core.model.UnhandledException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun CoroutineScope.launchSafe(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
): Job {
    val exceptionHandler = context[CoroutineExceptionHandler] ?: coroutineUncaughtExceptionHandler
    val safeContext = if (BuildConfig.DEBUG) context else context + exceptionHandler

    return launch(safeContext, start, block)
}

fun <T> CoroutineScope.asyncSafe(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T,
): Deferred<T> {
    val exceptionHandler = context[CoroutineExceptionHandler] ?: coroutineUncaughtExceptionHandler
    val safeContext = if (BuildConfig.DEBUG) context else context + exceptionHandler

    return async(safeContext, start, block)
}

private val coroutineUncaughtExceptionHandler: CoroutineExceptionHandler =
    CoroutineExceptionHandler { _, throwable ->
        if (throwable is CancellationException ||
            throwable is Error
        ) {
            throw throwable
        }
        Timber.e(UnhandledException(throwable))
    }
