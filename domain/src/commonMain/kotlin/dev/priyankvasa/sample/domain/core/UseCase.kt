package dev.priyankvasa.sample.domain.core

import dev.priyankvasa.sample.data.core.coroutines.CoroutineDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Invoking with [invoke] executes the use case on background thread (IO dispatcher; can be changed)
 * and returns a result of expected type [R]
 *
 * Override this implementation in cases where we need a single value result.
 *
 * The default dispatcher can be changed by overriding [getTaskCoroutineDispatcher]
 *
 * @throws Throwable depending on the usecase's [execute] implementation
 *
 * @param coroutineDispatcherProvider use [CoroutineDispatcherProvider.Default] for app code
 *   which maps 1-1 to [kotlinx.coroutines.Dispatchers]
 */
abstract class UseCase<in P, R>(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(parameters: P): R =
        withContext(getTaskCoroutineDispatcher()) {
            execute(parameters)
        }

    /**
     * Override this to set the code to be executed.
     */
    protected abstract suspend fun execute(params: P): R

    open fun getTaskCoroutineDispatcher(): CoroutineDispatcher =
        coroutineDispatcherProvider.default()
}
