package dev.priyankvasa.sample.data.auth

import dev.priyankvasa.sample.data.auth.model.SigninUser
import dev.priyankvasa.sample.data.auth.model.SignupUser
import dev.priyankvasa.sample.data.auth.model.UserAuthTokensEntity

internal interface AuthService : dev.priyankvasa.sample.data.auth.AuthTokensManager {
    suspend fun signupUser(user: SignupUser): Result<Unit>

    suspend fun signInUser(user: SigninUser): Result<UserAuthTokensEntity>

    suspend fun signOutUser(): Result<Unit>
}
