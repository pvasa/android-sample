package dev.priyankvasa.sample.data.core.util

import kotlinx.coroutines.CancellationException

fun Result<*>.mapToUnit(): Result<Unit> = map {}

/**
 * Like [runCatching], but with proper coroutines cancellation handling.
 * Also catches [Exception] instead of [Throwable].
 *
 * Cancellation exceptions need to be rethrown.
 * See https://github.com/Kotlin/kotlinx.coroutines/issues/1814.
 */
inline fun <R> runAppTaskCatching(
    retry: Int = 0,
    block: () -> R,
): Result<R> = retryAppTask(retry) {
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}

inline fun <R> retryAppTask(retry: Int = 1, task: () -> Result<R>): Result<R> {
    var result = task()

    var tryCount = 1
    while (result.isFailure && tryCount <= retry) {
        result = task()
        ++tryCount
    }

    return result
}

/**
 * Like [runCatching], but with proper coroutines cancellation handling.
 * Also catches [Exception] instead of [Throwable].
 *
 * Cancellation exceptions need to be rethrown.
 * See https://github.com/Kotlin/kotlinx.coroutines/issues/1814.
 */
inline fun <T, reified R> T.runAppTaskCatching(block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}

/**
 * Like [mapCatching], but uses [runAppTaskCatching] instead of [runCatching].
 */
inline fun <T, reified R> Result<T>.mapResultCatching(transform: (value: T) -> R): Result<R> {
    val successResult = getOrNull()
    return when {
        successResult != null -> runAppTaskCatching { transform(successResult) }
        else -> Result.failure(exceptionOrNull() ?: error("Unreachable state"))
    }
}
