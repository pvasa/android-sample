package dev.priyankvasa.sample.data.auth.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class RefreshToken(val token: String)
