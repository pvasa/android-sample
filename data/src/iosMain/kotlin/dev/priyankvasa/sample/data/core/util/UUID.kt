package dev.priyankvasa.sample.data.core.util

import platform.Foundation.NSUUID

actual object UUID {

    actual fun random(): String = NSUUID().UUIDString()
}
