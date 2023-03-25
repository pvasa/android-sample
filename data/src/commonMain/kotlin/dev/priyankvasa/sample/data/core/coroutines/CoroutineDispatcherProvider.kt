package dev.priyankvasa.sample.data.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal expect val IODispatcher: CoroutineDispatcher

/**
 * Provides the ability to inject Dispatchers for easier testing.
 */
interface CoroutineDispatcherProvider {
    fun main(): CoroutineDispatcher = Dispatchers.Main.immediate
    fun default(): CoroutineDispatcher = Dispatchers.Default
    fun io(): CoroutineDispatcher = IODispatcher
    fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined

    companion object Default : CoroutineDispatcherProvider
}
