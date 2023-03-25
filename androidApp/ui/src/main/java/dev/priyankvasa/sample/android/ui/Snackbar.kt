package dev.priyankvasa.sample.android.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun Snackbar(
    message: String,
    snackbarHostState: SnackbarHostState,
    duration: SnackbarDuration = SnackbarDuration.Short,
    onDismiss: (() -> Unit)? = null,
    actionLabel: String? = null,
    onPerformAction: (() -> Unit)? = null,
) {
    LaunchedEffect(
        key1 = message,
        block = {
            when (
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    withDismissAction = onDismiss != null,
                    duration = duration,
                )
            ) {
                SnackbarResult.Dismissed -> onDismiss?.invoke()
                SnackbarResult.ActionPerformed -> onPerformAction?.invoke()
            }
        },
    )
}
