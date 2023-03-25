package dev.priyankvasa.sample.data.core.model

import kotlinx.serialization.json.Json

internal data class RemoteApiConfig(
    val baseUrl: String,
    val logInfo: Boolean = true,
    val logHeaders: Boolean = true,
    val logBody: Boolean = true,
    val json: Json = Json.Default,
)
