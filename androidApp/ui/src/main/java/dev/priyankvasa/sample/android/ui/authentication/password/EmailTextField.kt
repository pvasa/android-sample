package dev.priyankvasa.sample.android.ui.authentication.password

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import dev.priyankvasa.sample.android.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailTextField(
    modifier: Modifier = Modifier,
    email: String,
    onEmailChange: (String) -> Unit = {},
    isEnabled: Boolean = true,
    imeAction: ImeAction = ImeAction.None,
    onImeAction: () -> Unit = {},
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .horizontalScroll(rememberScrollState()),
        value = email,
        onValueChange = onEmailChange,
        placeholder = { Text(text = stringResource(id = R.string.email_address)) },
        enabled = isEnabled,
        maxLines = 1,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = "email input icon",
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction,
        ),
        keyboardActions = KeyboardActions { onImeAction() },
    )
}
