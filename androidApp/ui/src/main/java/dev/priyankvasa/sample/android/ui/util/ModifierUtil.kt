package dev.priyankvasa.sample.android.ui.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import dev.priyankvasa.sample.android.ui.core.model.TaskState
import dev.priyankvasa.sample.android.ui.core.model.isRunning

fun Modifier.blurIfLoading(taskState: TaskState): Modifier =
    if (taskState.isRunning) blur(4.dp) else this
