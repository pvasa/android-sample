package dev.priyankvasa.sample.data.core.util

import dev.priyankvasa.sample.data.core.model.AuthenticationException
import dev.priyankvasa.sample.data.core.model.UnknownApiException
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.http.isSuccess

internal suspend inline fun <reified T> HttpStatement.executeAsResult(
    retry: Int = 0,
): Result<T> =
    runAppTaskCatching(retry) {
        val response = execute()

        val status = response.status

        when {
            status.isSuccess() -> body()

            status == Unauthorized -> throw AuthenticationException(response.bodyAsText())

            else -> throw UnknownApiException(status.value, response.bodyAsText())
        }
    }
