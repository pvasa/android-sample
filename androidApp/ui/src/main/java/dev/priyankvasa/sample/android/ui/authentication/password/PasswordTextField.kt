package dev.priyankvasa.sample.android.ui.authentication.password

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import dev.priyankvasa.sample.android.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    password: String,
    onPasswordChange: (String) -> Unit = {},
    isEnabled: Boolean = true,
    imeAction: ImeAction,
    onImeAction: () -> Unit,
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        value = password,
        onValueChange = onPasswordChange,
        placeholder = { Text(text = stringResource(id = R.string.password)) },
        enabled = isEnabled,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.VpnKey,
                contentDescription = "password input icon",
            )
        },
        trailingIcon = {
            IconToggleButton(
                checked = isPasswordVisible,
                onCheckedChange = { isPasswordVisible = it },
            ) {
                val icon = if (isPasswordVisible) {
                    Icons.Filled.VisibilityOff
                } else {
                    Icons.Filled.Visibility
                }

                Icon(imageVector = icon, contentDescription = "password toggle button")
            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction,
        ),
        keyboardActions = KeyboardActions { onImeAction() },
    )
}
