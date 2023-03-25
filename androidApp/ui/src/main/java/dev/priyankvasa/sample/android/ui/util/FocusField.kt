package dev.priyankvasa.sample.android.ui.util

interface FocusField {
    val previous: FocusField get() = this
    val next: FocusField get() = this

    val left: FocusField get() = previous
    val right: FocusField get() = next

    val up: FocusField get() = previous
    val down: FocusField get() = next

    val out: FocusField get() = previous
    val `in`: FocusField get() = next

    companion object None : FocusField
}
