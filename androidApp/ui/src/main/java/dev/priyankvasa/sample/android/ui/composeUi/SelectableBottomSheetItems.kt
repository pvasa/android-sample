package dev.priyankvasa.sample.android.ui.composeUi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Suppress("unused")
@Composable
fun <T> ColumnScope.SelectableBottomSheetItems(
    title: String? = null,
    items: List<T>,
    isSelected: (T) -> Boolean,
    onSelectItem: (T) -> Unit,
    onCancel: () -> Unit,
) {
    val contentPadding = 12.dp

    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (!title.isNullOrBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(contentPadding),
            )
        }

        IconButton(onClick = onCancel, modifier = Modifier.wrapContentSize()) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "close button",
            )
        }
    }

    items.forEach { item ->
        val isItemSelected = isSelected(item)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable { onSelectItem(item) },
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(contentPadding),
                text = item.toString(),
                color = if (isItemSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isItemSelected) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}
