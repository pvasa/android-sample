package dev.priyankvasa.sample.data.auth.model

import kotlinx.serialization.Serializable

@Serializable
internal data class SignupUser(
    val emailAddress: String,
    val familyName: String,
    val givenName: String,
    val password: String,
)
