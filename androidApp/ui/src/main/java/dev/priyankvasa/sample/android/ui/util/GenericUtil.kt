package dev.priyankvasa.sample.android.ui.util

import dev.priyankvasa.sample.android.ui.BuildConfig

inline fun debug(block: () -> Unit) {
    if (BuildConfig.DEBUG) block()
}
