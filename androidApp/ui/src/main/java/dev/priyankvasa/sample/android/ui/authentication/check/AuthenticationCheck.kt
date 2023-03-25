package dev.priyankvasa.sample.android.ui.authentication.check

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.priyankvasa.sample.android.ui.LocalNavController
import dev.priyankvasa.sample.android.ui.authentication.Authentication
import dev.priyankvasa.sample.android.ui.composeUi.LoadingOverlay
import dev.priyankvasa.sample.android.ui.core.model.TaskState

@Composable
fun AuthenticationCheck(viewModel: AuthenticationCheckViewModel) {
    val navigateOnAuth by viewModel.navigateOnAuth.collectAsStateWithLifecycle()
    val authCheckState by viewModel.userAuthCheckState.collectAsStateWithLifecycle()

    val navController = LocalNavController.current

    if (authCheckState is TaskState.Running) {
        LoadingOverlay()
    }

    LaunchedEffect(key1 = navigateOnAuth) {
        navigateOnAuth?.onSuccess {
            navigateToRouteOnAuthSuccess(navController, viewModel.navRouteOnAuth)
        }
            ?.onFailure {
                navigateToAuthenticationMethods(navController, viewModel.navRouteOnAuth)
            }
    }
}

private fun navigateToRouteOnAuthSuccess(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(Authentication()) {
            inclusive = true
        }
        anim {
            enter = android.R.anim.fade_in
            popExit = android.R.anim.fade_out
            exit = android.R.anim.fade_out
        }
    }
}

private fun navigateToAuthenticationMethods(
    navController: NavController,
    navRouteOnAuth: String,
) {
    navController.navigate(Authentication.Methods(navRouteOnAuth)) {
        popUpTo(Authentication.Check()) {
            inclusive = true
        }
        anim {
            enter = android.R.anim.slide_in_left
            popExit = android.R.anim.fade_out
            exit = android.R.anim.fade_out
        }
    }
}
