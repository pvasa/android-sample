package dev.priyankvasa.sample.data.core.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun createBaseHttpClient(): HttpClient =
    HttpClient(OkHttp)
