package dev.priyankvasa.sample.data.core.coroutines

import io.github.aakira.napier.Napier
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Flow<T>.asCFlow(): CFlow<T> = CFlow(this)

class CFlow<T>(private val origin: Flow<T>) : Flow<T> by origin {

    fun watch(block: (T) -> Unit): Closeable {
        val job = Job(/*ConferenceService.coroutineContext[Job]*/)

        val errorHandler = CoroutineExceptionHandler { _, throwable ->
            Napier.e("", throwable)
            throw throwable
        }

        CoroutineScope(Dispatchers.Main + job).launch(errorHandler) {
            collect { block(it) }
        }

        return object : Closeable {
            override fun close() {
                job.cancel("Closing!")
            }
        }
    }
}
