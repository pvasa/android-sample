package dev.priyankvasa.sample.android.ui.authentication

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import dev.priyankvasa.sample.android.ui.authentication.check.AuthenticationCheck
import dev.priyankvasa.sample.android.ui.authentication.methods.AuthenticationMethods
import dev.priyankvasa.sample.android.ui.authentication.password.PasswordAuthentication

fun NavGraphBuilder.authenticationNavigation() {
    navigation(
        route = Authentication(),
        startDestination = Authentication.Check(),
        arguments = Authentication.namedNavArgs,
    ) {
        composable(
            route = Authentication.Check(),
            arguments = Authentication.namedNavArgs,
        ) {
            AuthenticationCheck(hiltViewModel(it))
        }

        composable(
            route = Authentication.Methods(),
            arguments = Authentication.namedNavArgs,
        ) {
            AuthenticationMethods(hiltViewModel(it))
        }

        composable(
            route = Authentication.Password(),
            arguments = Authentication.namedNavArgs,
        ) {
            PasswordAuthentication(hiltViewModel(it))
        }
    }
}
