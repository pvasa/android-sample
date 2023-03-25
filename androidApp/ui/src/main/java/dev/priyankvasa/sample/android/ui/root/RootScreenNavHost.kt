package dev.priyankvasa.sample.android.ui.root

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.priyankvasa.sample.android.ui.authentication.Authentication
import dev.priyankvasa.sample.android.ui.authentication.authenticationNavigation
import dev.priyankvasa.sample.android.ui.home.Home
import dev.priyankvasa.sample.android.ui.home.HomeScreen
import dev.priyankvasa.sample.android.ui.search.Search
import dev.priyankvasa.sample.android.ui.search.SearchScreen

@Composable
fun RootScreenNavHost(navHostController: NavHostController) {
    NavHost(navHostController, startDestination = Authentication(), route = Root()) {
        authenticationNavigation()

        composable(Home()) {
            HomeScreen(hiltViewModel(it))
        }

        composable(Search()) {
            SearchScreen(hiltViewModel(it))
        }
    }
}
