package dev.priyankvasa.sample.data.sample.repository

import dev.priyankvasa.sample.data.core.coroutines.CoroutineDispatcherProvider
import dev.priyankvasa.sample.data.sample.datasource.remote.SampleService
import kotlinx.coroutines.withContext

class SampleRepository(
    private val sampleService: SampleService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {

    suspend fun getSample(): String =
        withContext(dispatcherProvider.io()) {
            sampleService.getSample()
        }
}
