package dev.priyankvasa.sample.data.auth

import io.ktor.client.statement.HttpResponse

internal interface AuthTokensProvider {
    fun isSecurePath(requestUrlEncodedPath: String): Boolean
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun refreshTokens(): HttpResponse

    suspend fun refreshTokensWithResult(): Result<Unit>
}

internal interface AuthTokensUpdater {
    suspend fun setAccessToken(token: String?)
    suspend fun setRefreshToken(token: String?)
}
