package dev.priyankvasa.sample.android.ui.core.model

sealed interface TaskState {
    object Idle : TaskState

    object Running : TaskState

    data class Failed(val cause: Throwable) : TaskState
}

val TaskState.isRunning: Boolean get() = this is TaskState.Running
