package dev.priyankvasa.sample.data.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SigninUser(
    @SerialName("emailAddress")
    val emailAddress: String,
    val password: String,
    @SerialName("clientInstanceId")
    val appId: String,
)
