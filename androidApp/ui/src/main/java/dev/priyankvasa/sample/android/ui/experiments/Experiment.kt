package dev.priyankvasa.sample.android.ui.experiments

sealed interface Experiment {
    fun isEnabled(): Boolean = false
}
