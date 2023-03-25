package dev.priyankvasa.sample.android.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.priyankvasa.sample.android.ui.LocalNavController
import dev.priyankvasa.sample.android.ui.R
import dev.priyankvasa.sample.android.ui.authentication.Authentication
import dev.priyankvasa.sample.android.ui.authentication.password.UserAuthenticationState
import dev.priyankvasa.sample.android.ui.authentication.password.UserNotAuthenticated
import dev.priyankvasa.sample.android.ui.root.Root
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNavigationDrawerContent(
    drawerState: DrawerState,
    viewModel: HomeViewModel,
) {
    val navController = LocalNavController.current

    val coroutineScope = rememberCoroutineScope()

    val authenticationState by viewModel.authenticationState.collectAsStateWithLifecycle()

    AuthenticationOnSignOut(authenticationState, navController)

    Column {
        DrawerCloseButton(
            onClick = {
                coroutineScope.launch { drawerState.close() }
            },
        )

        DrawerItem(
            title = R.string.account,
            icon = Icons.Filled.AccountCircle,
            onClick = { /*TODO*/ },
        )

        DrawerItem(
            title = R.string.settings,
            icon = Icons.Filled.Settings,
            onClick = { /*TODO*/ },
        )

        DrawerItem(
            title = R.string.logout,
            icon = Icons.Outlined.ExitToApp,
            onClick = viewModel::signOut,
        )
    }
}

@Composable
private fun DrawerCloseButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.CenterEnd,
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.wrapContentSize(),
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "close button",
                modifier = Modifier.wrapContentSize(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrawerItem(
    @StringRes title: Int,
    icon: ImageVector,
    iconContentDescription: String = stringResource(id = title),
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick),
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = iconContentDescription,
            )
        },
        headlineText = {
            Text(text = stringResource(id = title))
        },
    )
}

@Composable
fun AuthenticationOnSignOut(
    authenticationState: UserAuthenticationState,
    navController: NavController,
) {
    LaunchedEffect(key1 = authenticationState) {
        if (authenticationState is UserNotAuthenticated) {
            navController.navigate(Authentication(navRouteOnAuth = Home())) {
                popUpTo(Root())
            }
        }
    }
}
