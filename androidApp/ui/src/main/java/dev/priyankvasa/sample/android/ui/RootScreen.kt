package dev.priyankvasa.sample.android.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import dev.priyankvasa.sample.android.ui.root.RootScreenNavHost
import timber.log.Timber

@Composable
fun RootScreen() {
    val navController = rememberNavController().apply {
        addOnDestinationChangedListener { controller, destination, arguments ->
            Timber.d("Naw destination: ${destination.route}\nArgs: $arguments")
            Timber.d("Nav graph\n${controller.backQueue.map { it.destination.route }.joinToString("\n")}")
        }
    }

    Surface {
        CompositionLocalProvider(
            LocalNavController provides navController,
        ) {
            RootScreenNavHost(navController)
        }
    }
}
