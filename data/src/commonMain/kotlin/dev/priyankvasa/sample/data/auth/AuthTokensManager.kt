package dev.priyankvasa.sample.data.auth

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import dev.priyankvasa.sample.data.auth.model.RefreshToken
import dev.priyankvasa.sample.data.auth.model.UserAuthTokensEntity
import dev.priyankvasa.sample.data.core.model.AuthenticationException
import dev.priyankvasa.sample.data.core.model.SettingsKeys
import dev.priyankvasa.sample.data.core.remote.rest.RestApi
import dev.priyankvasa.sample.data.core.util.mapResultCatching
import dev.priyankvasa.sample.data.core.util.runAppTaskCatching
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.http.isSuccess
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal interface AuthTokensManager :
    dev.priyankvasa.sample.data.auth.AuthTokensProvider,
    dev.priyankvasa.sample.data.auth.AuthTokensUpdater

internal class SampleServiceAuthTokensManagerImpl(
    private val settings: Settings,
    private val restApi: RestApi,
) : dev.priyankvasa.sample.data.auth.AuthTokensManager {
    private val accessTokenLock = Mutex(locked = false)

    private val accessTokenKey = SettingsKeys.AuthAccessToken
    private val refreshTokenKey = SettingsKeys.AuthRefreshToken

    private val refreshAuthPath: String = "/user/authorize/refresh"

    override fun isSecurePath(requestUrlEncodedPath: String): Boolean =
        !requestUrlEncodedPath.contains(refreshAuthPath)

    override suspend fun getAccessToken(): String? =
        accessTokenLock.withLock {
            settings[accessTokenKey]
        }

    override suspend fun setAccessToken(token: String?) {
        settings[accessTokenKey] = token
    }

    override suspend fun getRefreshToken(): String? =
        settings[refreshTokenKey]

    override suspend fun setRefreshToken(token: String?) {
        settings[refreshTokenKey] = token
    }

    override suspend fun refreshTokens(): HttpResponse =
        accessTokenLock.withLock {
            refreshTokensInternal()
        }

    override suspend fun refreshTokensWithResult(): Result<Unit> =
        runAppTaskCatching { refreshTokens() }
            .mapResultCatching { response ->
                when {
                    response.status.isSuccess() -> Unit
                    else -> throw AuthenticationException(response.bodyAsText())
                }
            }

    private suspend fun refreshTokensInternal(): HttpResponse {
        val refreshToken = getRefreshToken() ?: run {
            removeTokens()
            throw AuthenticationException("Refresh token is null.")
        }

        return restApi.post(
            encodedPath = refreshAuthPath,
            body = RefreshToken(refreshToken),
        ).execute().also { httpResponse ->
            when {
                httpResponse.status.isSuccess() -> {
                    val tokens = httpResponse.body<UserAuthTokensEntity>()
                    setRefreshToken(tokens.refreshToken)
                    setAccessToken(tokens.accessToken)
                }
                httpResponse.status == Unauthorized -> {
                    removeTokens()
                }
            }
        }
    }

    private suspend fun removeTokens() {
        setRefreshToken(null)
        setAccessToken(null)
    }
}
