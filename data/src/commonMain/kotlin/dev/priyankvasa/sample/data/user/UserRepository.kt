package dev.priyankvasa.sample.data.user

import com.russhwolf.settings.Settings
import com.russhwolf.settings.string
import dev.priyankvasa.sample.data.auth.AuthService
import dev.priyankvasa.sample.data.auth.model.SigninUser
import dev.priyankvasa.sample.data.auth.model.SignupUser
import dev.priyankvasa.sample.data.core.coroutines.CoroutineDispatcherProvider
import dev.priyankvasa.sample.data.core.model.SettingsKeys
import dev.priyankvasa.sample.data.core.util.UUID
import dev.priyankvasa.sample.data.core.util.mapToUnit
import kotlinx.coroutines.withContext

class UserRepository internal constructor(
    private val authService: AuthService,
    settings: Settings,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    private var appId: String by settings.string(SettingsKeys.AppId, DEFAULT_APP_ID)

    init {
        if (appId.isBlank() || appId == DEFAULT_APP_ID) {
            appId = UUID.random()
        }
    }

    suspend fun refreshAuthSession(): Result<Unit> =
        withContext(dispatcherProvider.io()) {
            authService.refreshTokensWithResult()
        }

    suspend fun signupUser(
        emailAddress: String,
        familyName: String,
        givenName: String,
        password: String,
    ): Result<Unit> =
        withContext(dispatcherProvider.io()) {
            authService.signupUser(
                SignupUser(
                    emailAddress,
                    familyName,
                    givenName,
                    password,
                ),
            )
        }

    suspend fun signInUser(emailAddress: String, password: String): Result<Unit> =
        withContext(dispatcherProvider.io()) {
            val user = SigninUser(
                emailAddress,
                password,
                appId,
            )

            val result = authService.signInUser(user)

            result.onSuccess {
                authService.setAccessToken(it.accessToken)
                authService.setRefreshToken(it.refreshToken)
            }
                .mapToUnit()
        }

    suspend fun signOut() {
        withContext(dispatcherProvider.io()) {
            authService.signOutUser()
            authService.setAccessToken(null)
            authService.setRefreshToken(null)
        }
    }

    private companion object {
        const val DEFAULT_APP_ID = ""
    }
}
