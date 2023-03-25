package dev.priyankvasa.sample.data.util

import androidx.core.util.PatternsCompat

actual object Patterns {
    actual val EMAIL_ADDRESS: Regex =
        PatternsCompat.EMAIL_ADDRESS.toRegex()
}
