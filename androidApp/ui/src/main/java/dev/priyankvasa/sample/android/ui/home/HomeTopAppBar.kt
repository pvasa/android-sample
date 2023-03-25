package dev.priyankvasa.sample.android.ui.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.priyankvasa.sample.android.ui.LocalNavController
import dev.priyankvasa.sample.android.ui.ModalDrawerToggleButton
import dev.priyankvasa.sample.android.ui.R
import dev.priyankvasa.sample.android.ui.search.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(drawerState: DrawerState) {
    val navController = LocalNavController.current

    TopAppBar(
        title = {
            Text(stringResource(id = R.string.title))
        },
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        navigationIcon = {
            ModalDrawerToggleButton(drawerState = drawerState)
        },
        actions = {
            IconButton(onClick = { navController.navigate(Search()) }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = stringResource(id = R.string.desc_search_button),
                )
            }
        },
    )
}
