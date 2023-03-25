package dev.priyankvasa.sample.data.core.model

sealed class ApiException(message: String) : Exception(message)

class AuthenticationException(message: String) : ApiException(message)

class UnknownApiException(
    val httpStatusCode: Int,
    message: String,
) : ApiException(message)
