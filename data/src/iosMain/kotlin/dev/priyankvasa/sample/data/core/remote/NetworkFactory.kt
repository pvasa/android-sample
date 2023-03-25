package dev.priyankvasa.sample.data.core.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

internal actual fun createBaseHttpClient(): HttpClient =
    HttpClient(Darwin) {
        engine {
            configureRequest { // this: NSMutableURLRequest
                setAllowsCellularAccess(true)
                setAllowsExpensiveNetworkAccess(true)
            }
        }
    }
