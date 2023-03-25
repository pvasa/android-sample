package dev.priyankvasa.sample.android.ui.composeUi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Chip(
    startIcon: () -> ImageVector? = { null },
    isStartIconEnabled: Boolean = false,
    startIconTint: Color = Color.Unspecified,
    onStartIconClicked: () -> Unit = { },
    endIcon: () -> ImageVector? = { null },
    isEndIconEnabled: Boolean = false,
    endIconTint: Color = Color.Unspecified,
    onEndIconClicked: () -> Unit = { },
    color: Color = MaterialTheme.colorScheme.surface,
    contentDescription: String,
    label: String,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    isClickable: Boolean = false,
    onClick: () -> Unit = { },
    elevation: Dp = 4.dp,
) {
    Surface(
        modifier = Modifier.clickable(
            enabled = isClickable,
            onClick = { onClick() },
        ),
        tonalElevation = elevation,
        shape = MaterialTheme.shapes.small,
        color = color,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val leader = startIcon()
            val trailer = endIcon()

            if (leader != null) {
                Icon(
                    leader,
                    contentDescription = contentDescription,
                    tint = startIconTint,
                    modifier = Modifier
                        .clickable(enabled = isStartIconEnabled, onClick = onStartIconClicked)
                        .padding(horizontal = 4.dp),
                )
            }

            Text(
                label,
                color = labelColor,
                modifier = Modifier.padding(8.dp),
            )

            if (trailer != null) {
                Icon(
                    trailer,
                    contentDescription = contentDescription,
                    tint = endIconTint,
                    modifier = Modifier
                        .clickable(enabled = isEndIconEnabled, onClick = onEndIconClicked)
                        .padding(horizontal = 4.dp),
                )
            }
        }
    }
}

@Composable
fun SelectableChip(
    label: String,
    contentDescription: String,
    selected: Boolean,
    onClick: (shouldSelect: Boolean) -> Unit,
    elevation: Dp = 0.dp,
) {
    Chip(
        color = if (selected) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.surface,
        labelColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        elevation = elevation,
        contentDescription = contentDescription,
        label = label,
        isClickable = true,
        onClick = { onClick(!selected) },
    )
}

@Composable
fun RemovableChip(
    label: String,
    contentDescription: String,
    onRemove: () -> Unit,
) {
    Chip(
        endIcon = { Icons.Default.HighlightOff },
        endIconTint = Color.Black.copy(alpha = 0.5f),
        contentDescription = contentDescription,
        label = label,
        onEndIconClicked = { onRemove() },
    )
}
