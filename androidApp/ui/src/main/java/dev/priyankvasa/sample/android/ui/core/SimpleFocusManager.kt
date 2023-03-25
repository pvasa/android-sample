package dev.priyankvasa.sample.android.ui.core

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import dev.priyankvasa.sample.android.ui.util.FocusField

fun Modifier.simpleFocusHandler(
    focusManager: SimpleFocusManager<*>,
    focusField: FocusField,
    onFocusChanged: ((FocusState) -> Unit)? = null,
): Modifier =
    focusRequester(focusManager.requester(focusField))
        .onFocusChanged { focusState ->
            focusManager.focusChanged(focusField, focusState)
            onFocusChanged?.invoke(focusState)
        }

class SimpleFocusManager<T : FocusField>(
    focusFields: Array<T>,
    private val onClearFocus: (forcedClear: Boolean) -> Unit = {},
) : FocusManager {
    private val requesters =
        focusFields.associateWith { FocusRequester() }

    private var currentFocusField: FocusField = FocusField.None

    override fun clearFocus(force: Boolean) {
        currentFocusField = FocusField.None
        onClearFocus(force)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    override fun moveFocus(focusDirection: FocusDirection): Boolean {
        val moveFocusToField = when (focusDirection) {
            FocusDirection.Next -> currentFocusField.next
            FocusDirection.Previous -> currentFocusField.previous
            FocusDirection.Left -> currentFocusField.left
            FocusDirection.Right -> currentFocusField.right
            FocusDirection.Up -> currentFocusField.up
            FocusDirection.Down -> currentFocusField.down
            FocusDirection.In -> currentFocusField.`in`
            FocusDirection.Out -> currentFocusField.out
            else -> error("Unknown focus direction $focusDirection!")
        }

        return moveFocus(moveFocusToField)
    }

    fun requester(field: FocusField) = requesters[field]
        ?: error("Unknown focus field $field. It must be present in provided orderedFocusFields.")

    fun focusChanged(focusField: FocusField, focusState: FocusState) {
        if (focusState.isFocused) {
            currentFocusField = focusField
        } else if (currentFocusField == focusField) {
            currentFocusField = FocusField.None
        }
    }

    private fun moveFocus(toField: FocusField): Boolean {
        requesters[toField]?.requestFocus()
        val moved = toField != currentFocusField
        currentFocusField = toField

        return moved
    }
}
