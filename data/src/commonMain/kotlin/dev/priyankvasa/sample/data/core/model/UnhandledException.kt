package dev.priyankvasa.sample.data.core.model

class UnhandledException(
    cause: Throwable? = null,
    message: String? = null,
) : RuntimeException(message, cause)
