package dev.priyankvasa.sample.data.ktor.util

import io.ktor.client.plugins.DefaultRequest
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom

internal fun DefaultRequest.DefaultRequestBuilder.urlWithBase(baseUrl: String) {
    val baseUrlWithPath = URLBuilder(baseUrl).apply {
        encodedPath += "${url.encodedPath}/"
    }

    url.takeFrom(baseUrlWithPath)
}
