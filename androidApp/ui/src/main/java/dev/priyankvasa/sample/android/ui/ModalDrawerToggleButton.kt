package dev.priyankvasa.sample.android.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDrawerToggleButton(
    drawerState: DrawerState,
) {
    val coroutineScope = rememberCoroutineScope()

    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "navigation drawer toggle button",
        )
    }
}
