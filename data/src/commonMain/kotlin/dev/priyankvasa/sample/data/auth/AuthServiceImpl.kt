package dev.priyankvasa.sample.data.auth

import dev.priyankvasa.sample.data.auth.model.SigninUser
import dev.priyankvasa.sample.data.auth.model.SignupUser
import dev.priyankvasa.sample.data.auth.model.UserAuthTokensEntity
import dev.priyankvasa.sample.data.core.remote.rest.RestApi
import dev.priyankvasa.sample.data.core.util.executeAsResult

internal class AuthServiceImpl(
    private val restApi: RestApi,
    private val authTokensManager: dev.priyankvasa.sample.data.auth.AuthTokensManager,
) : dev.priyankvasa.sample.data.auth.AuthService, dev.priyankvasa.sample.data.auth.AuthTokensManager by authTokensManager {
    override suspend fun signupUser(user: SignupUser): Result<Unit> =
        restApi.put(encodedPath = "/user/create", body = user)
            .executeAsResult()

    override suspend fun signInUser(user: SigninUser): Result<UserAuthTokensEntity> =
        restApi.post(encodedPath = "/user/authenticate", body = user)
            .executeAsResult()

    override suspend fun signOutUser(): Result<Unit> {
        authTokensManager.setRefreshToken(null)
        authTokensManager.setAccessToken(null)

        return restApi.get(encodedPath = "/user/logout")
            .executeAsResult(retry = 3)
    }
}
