@file:SuppressLint("ComposableNaming")

package dev.priyankvasa.sample.android.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview

private const val PREVIEW_TEXT = "Almost before we knew it, we had left the ground."

@Preview
@Composable
private fun titleLarge() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.titleLarge)
        }
    }
}

@Preview
@Composable
private fun titleMedium() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview
@Composable
private fun titleSmall() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.titleSmall)
        }
    }
}

@Preview
@Composable
private fun headlineLarge() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.headlineLarge)
        }
    }
}

@Preview
@Composable
private fun headlineMedium() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.headlineMedium)
        }
    }
}

@Preview
@Composable
private fun headlineSmall() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.headlineSmall)
        }
    }
}

@Preview
@Composable
private fun bodyLarge() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview
@Composable
private fun bodyMedium() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview
@Composable
private fun bodySmall() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview
@Composable
private fun labelLarge() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.labelLarge)
        }
    }
}

@Preview
@Composable
private fun labelMedium() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.labelMedium)
        }
    }
}

@Preview
@Composable
private fun labelSmall() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview
@Composable
private fun displayLarge() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.displayLarge)
        }
    }
}

@Preview
@Composable
private fun displayMedium() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.displayMedium)
        }
    }
}

@Preview
@Composable
private fun displaySmall() {
    AppTheme {
        Surface(Modifier.wrapContentSize()) {
            StylePreview(MaterialTheme.typography.displaySmall)
        }
    }
}

@Composable
private fun StylePreview(style: TextStyle) {
    Text(text = PREVIEW_TEXT, style = style)
}
