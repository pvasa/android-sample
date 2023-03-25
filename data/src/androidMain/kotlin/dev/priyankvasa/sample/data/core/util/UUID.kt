package dev.priyankvasa.sample.data.core.util

import java.util.UUID

actual object UUID {

    actual fun random(): String = UUID.randomUUID().toString()
}
