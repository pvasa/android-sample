package dev.priyankvasa.sample.data.logging

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

actual fun enableDebugLogging() {
    Napier.base(DebugAntilog())
}
