package dev.priyankvasa.sample.android.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("Navigation controller not set!")
}
