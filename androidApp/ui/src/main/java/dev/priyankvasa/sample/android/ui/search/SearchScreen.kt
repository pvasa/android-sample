@file:OptIn(ExperimentalMaterial3Api::class)

package dev.priyankvasa.sample.android.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.priyankvasa.sample.android.ui.LocalNavController
import dev.priyankvasa.sample.android.ui.R
import dev.priyankvasa.sample.android.ui.Snackbar
import dev.priyankvasa.sample.android.ui.composeUi.SpacedListLazyColumn
import dev.priyankvasa.sample.android.ui.core.SimpleFocusManager
import dev.priyankvasa.sample.android.ui.core.model.TaskState
import dev.priyankvasa.sample.android.ui.core.simpleFocusHandler

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
) {
    val query: String by viewModel.query.collectAsStateWithLifecycle()
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { parentPadding ->
        Box(
            modifier = Modifier.padding(parentPadding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                SearchBar(
                    query,
                    searchState is TaskState.Running,
                    { newQuery -> viewModel.onQueryChange(newQuery) },
                    viewModel::submitQuery,
                )

                SearchPageContent(searchResults)

                when (searchState) {
                    is TaskState.Failed -> {
                        Snackbar(
                            message = stringResource(id = R.string.error_search_failed),
                            snackbarHostState = snackbarHostState,
                            duration = SnackbarDuration.Indefinite,
                            actionLabel = stringResource(id = R.string.try_again),
                            onPerformAction = { viewModel.submitQuery() },
                        )
                    }

                    else -> {
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    isSearching: Boolean,
    onQueryChange: (String) -> Unit,
    onSubmitQuery: () -> Unit,
) {
    val navController = LocalNavController.current
    val focusManager = LocalFocusManager.current

    val simpleFocusManager = remember {
        SimpleFocusManager(
            SearchPageFocusField.values(),
            focusManager::clearFocus,
        )
    }

    val focusField = SearchPageFocusField.SEARCH_QUERY

    val onSubmitInternal = {
        simpleFocusManager.clearFocus()
        onSubmitQuery()
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 8.dp, end = 8.dp, start = 8.dp),
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.small,
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .simpleFocusHandler(simpleFocusManager, focusField),
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = stringResource(id = R.string.hint_search_stock),
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { onSubmitInternal() },
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier = Modifier.size(24.dp),
                    onClick = { navController.navigateUp() },
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "close search page",
                    )
                }
            },
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                    )
                } else if (query.isNotBlank()) {
                    IconButton(
                        modifier = Modifier.size(24.dp),
                        onClick = { onQueryChange("") },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "clear query",
                        )
                    }
                }
            },
        )
    }

    LaunchedEffect(query) {
        if (query.isBlank()) {
            with(simpleFocusManager.requester(focusField)) {
                requestFocus()
                captureFocus()
            }
        } else {
            simpleFocusManager.requester(focusField).freeFocus()
        }
    }
}

@Composable
private fun SearchPageContent(searchStocks: List<String>) {
    val navController = LocalNavController.current

    SpacedListLazyColumn(
        modifier = Modifier.fillMaxSize(),
        listItems = searchStocks,
        onItem = { item ->
            Card(Modifier.padding(8.dp)) {
                Text(text = item)
            }
        },
    )
}
