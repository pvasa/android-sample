package dev.priyankvasa.sample.data.auth.model

import kotlinx.serialization.Serializable

@Serializable
internal data class UserAuthTokensEntity(
    val accessToken: String,
    val refreshToken: String,
)
