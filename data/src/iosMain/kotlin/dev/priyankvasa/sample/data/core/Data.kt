package dev.priyankvasa.sample.data.core

import kotlin.native.concurrent.freeze

internal actual var internalConfig: Data.Config? = null
    set(value) {
        field = value.freeze()
    }
