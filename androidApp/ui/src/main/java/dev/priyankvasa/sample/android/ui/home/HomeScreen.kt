@file:OptIn(ExperimentalMaterial3Api::class)

package dev.priyankvasa.sample.android.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.priyankvasa.sample.android.ui.BackHandler
import dev.priyankvasa.sample.android.ui.R
import dev.priyankvasa.sample.android.ui.composeUi.LoadingOverlay
import dev.priyankvasa.sample.android.ui.core.model.TaskState
import dev.priyankvasa.sample.android.ui.core.model.isRunning
import dev.priyankvasa.sample.android.ui.util.blurIfLoading
import dev.priyankvasa.sample.android.ui.util.debug
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val navItems = remember {
        listOf(
            HomeNavDest.Dest1,
            HomeNavDest.Dest2,
        )
    }

    val navHostController = rememberNavController().apply {
        debug {
            addOnDestinationChangedListener { controller, destination, arguments ->
                Timber.d("New destination: ${destination.route}\nArgs: $arguments")
            }
        }
    }

    val logoutState by viewModel.signOutState.collectAsStateWithLifecycle()

    BackHandler(
        enabled = isBackHandlerEnabled(drawerState, logoutState),
        onBack = { coroutineScope.launch { drawerState.close() } },
    )

    if (logoutState.isRunning) {
        LoadingOverlay(loadingText = stringResource(id = R.string.logging_out))
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                content = {
                    HomeNavigationDrawerContent(drawerState, viewModel)
                },
            )
        },
        content = {
            Scaffold(
                modifier = Modifier.blurIfLoading(logoutState),
                topBar = { HomeTopAppBar(drawerState = drawerState) },
                bottomBar = {
                    HomeNavigationBar(navItems, navHostController)
                },
            ) { parentPadding ->
                HomeScreenNavHost(
                    modifier = Modifier.padding(parentPadding),
                    navHostController = navHostController,
                    navItems = navItems,
                )
            }
        },
    )
}

@Composable
fun HomeNavigationBar(
    screens: List<HomeNavDest>,
    navController: NavController,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        screens.forEach { screen ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = "",
                    )
                },
                label = {
                    Text(text = stringResource(id = screen.title))
                },
            )
        }
    }
}

sealed class HomeNavDest(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector,
) {
    object Dest1 : HomeNavDest(
        Home.Dest1(),
        R.string.dest1,
        Icons.Default.Favorite,
    )

    object Dest2 : HomeNavDest(
        Home.Dest2(),
        R.string.dest2,
        Icons.Default.Explore,
    )
}

private fun isBackHandlerEnabled(
    drawerState: DrawerState,
    logoutState: TaskState,
): Boolean =
    !logoutState.isRunning && (drawerState.isAnimationRunning || drawerState.isOpen)

@Composable
private fun HomeScreenNavHost(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    navItems: List<HomeNavDest>,
) {
    require(navItems.isNotEmpty())

    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = navItems.first().route,
    ) {
        navItems.forEach { dest ->
            when (dest) {
                HomeNavDest.Dest2 -> {
                    composable(dest.route) { backStackEntry ->
                        // dest 1 screen
                    }
                }
                HomeNavDest.Dest1 -> {
                    composable(dest.route) { backStackEntry ->
                        // dest 2 screen
                    }
                }
            }
        }
    }
}
