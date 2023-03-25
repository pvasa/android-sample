package dev.priyankvasa.sample.data.core.util

import io.ktor.http.ParametersBuilder

fun ParametersBuilder.appendAll(map: Map<String, Any?>) {
    map.forEach { (key, value) ->
        val valueString = if (value is Collection<*>) {
            value.joinToString(",")
        } else {
            value.toString()
        }

        append(key, valueString)
    }
}
