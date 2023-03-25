package dev.priyankvasa.sample.android.ui.composeUi

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T> SpacedListLazyColumn(
    modifier: Modifier = Modifier,
    listItems: List<T>,
    itemSpace: Dp = 8.dp,
    onItem: @Composable (T) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        content = {
            items(listItems) { item ->
                onItem(item)

                if (item !== listItems.last()) {
                    Spacer(modifier = Modifier.size(itemSpace))
                }
            }
        },
        contentPadding = PaddingValues(
            start = itemSpace,
            end = itemSpace,
            top = itemSpace,
            bottom = itemSpace,
        ),
    )
}
