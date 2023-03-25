@file:OptIn(ExperimentalMaterial3Api::class)

package dev.priyankvasa.sample.android.ui.authentication.password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.priyankvasa.sample.android.ui.LocalNavController
import dev.priyankvasa.sample.android.ui.R
import dev.priyankvasa.sample.android.ui.Snackbar
import dev.priyankvasa.sample.android.ui.authentication.Authentication
import dev.priyankvasa.sample.android.ui.core.SimpleFocusManager
import dev.priyankvasa.sample.android.ui.core.model.TaskState
import dev.priyankvasa.sample.android.ui.core.simpleFocusHandler

private val FormFieldSpace = 8.dp

private typealias AuthFormFocusManager = SimpleFocusManager<PasswordAuthFormFocusField>

@Composable
fun PasswordAuthentication(viewModel: PasswordAuthenticationViewModel) {
    val navController = LocalNavController.current

    val pageState: PasswordAuthPageState
        by viewModel.pageState.collectAsStateWithLifecycle()

    val authState: TaskState
        by viewModel.authenticationState.collectAsStateWithLifecycle()

    val userAuthenticationState: UserAuthenticationState
        by viewModel.userAuthenticationState.collectAsStateWithLifecycle()

    val emailAddress: String = viewModel.emailAddress

    var familyName: String by rememberSaveable { mutableStateOf("") }
    var givenName: String by rememberSaveable { mutableStateOf("") }
    var password: String by rememberSaveable { mutableStateOf("") }

    val areFieldsEnabled = authState !is TaskState.Running

    val snackbarHostState = remember { SnackbarHostState() }

    val localFocusManager = LocalFocusManager.current
    val focusManager = remember {
        SimpleFocusManager(
            PasswordAuthFormFocusField.values(),
            localFocusManager::clearFocus,
        )
    }

    LaunchedEffect(key1 = userAuthenticationState) {
        if (userAuthenticationState is UserAuthenticated) {
            navController.navigate(viewModel.navRouteOnAuth) {
                popUpTo(Authentication()) {
                    inclusive = true
                }
            }
        }
    }

    AuthenticationStateChanged(
        authState,
        snackbarHostState,
        viewModel::dismissAuthenticationFailure,
    )

    fun submit() {
        focusManager.clearFocus()
        viewModel.submit(emailAddress, familyName, givenName, password)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { parentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(parentPadding)
                .padding(vertical = 24.dp, horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            UserAuthHintText(pageState = pageState)

            Spacer(modifier = Modifier.size(32.dp))

            EmailText(emailAddress) {
                navController.navigateUp()
            }

            FieldSpacer()

            PasswordTextField(
                focusManager = focusManager,
                isEnabled = areFieldsEnabled,
                password = password,
                onPasswordChange = { password = it },
                imeAction = pageState.passwordFieldImeAction,
                onImeAction = {
                    when (pageState.passwordFieldImeAction) {
                        ImeAction.Send -> submit()
                        ImeAction.Next -> focusManager.moveFocus(FocusDirection.Next)
                    }
                },
            )

            if (pageState.page == PasswordAuthPage.SignUp) {
                FieldSpacer()

                GivenNameTextField(
                    focusManager = focusManager,
                    isEnabled = areFieldsEnabled,
                    text = givenName,
                    onTextChange = { givenName = it },
                )

                FieldSpacer()

                FamilyNameTextField(
                    focusManager = focusManager,
                    isEnabled = areFieldsEnabled,
                    text = familyName,
                    onTextChange = { familyName = it },
                )
            }

            LaunchedEffect(pageState) {
                requestFocusPasswordField(focusManager)
            }

            Spacer(modifier = Modifier.size(24.dp))

            SubmitButton(
                pageState = pageState,
                isLoading = authState is TaskState.Running,
                isEnabled = areFieldsEnabled,
                onClick = ::submit,
            )

            Spacer(modifier = Modifier.size(16.dp))

            PageToggleButton(
                pageState = pageState,
                isEnabled = areFieldsEnabled,
                onClick = viewModel::togglePage,
            )
        }
    }
}

private fun requestFocusPasswordField(focusManager: AuthFormFocusManager) {
    focusManager.requester(PasswordAuthFormFocusField.PASSWORD)
        .requestFocus()
}

@Composable
private fun AuthenticationStateChanged(
    newState: TaskState,
    snackbarHostState: SnackbarHostState,
    onDismissError: () -> Unit,
) {
    when (newState) {
        is TaskState.Failed -> {
            ErrorSnackbar(
                newState.cause,
                snackbarHostState,
                onDismissError,
            )
        }

        else -> {
            // do nothing
        }
    }
}

@Composable
private fun ErrorSnackbar(
    error: Throwable,
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit,
) {
    when (error) {
        PasswordAuthError.EmptyField -> {
            Snackbar(
                message = stringResource(id = R.string.error_auth_field_empty),
                snackbarHostState = snackbarHostState,
                onDismiss = onDismiss,
                actionLabel = stringResource(id = R.string.ok),
                onPerformAction = onDismiss,
            )
        }
    }
}

@Composable
private fun FieldSpacer() {
    Spacer(modifier = Modifier.size(FormFieldSpace))
}

@Composable
private fun UserAuthHintText(pageState: PasswordAuthPageState) {
    Text(
        text = pageState.pageHint(),
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.titleSmall,
    )
}

@Preview
@Composable
private fun UserAuthHintTextPreview() {
    UserAuthHintText(PasswordAuthPage.SignIn.state())
}

@Composable
private fun EmailText(
    emailAddress: String,
    modifier: Modifier = Modifier,
    onEditEmail: () -> Unit = {},
) {
    Row(modifier) {
        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = "email address icon",
        )

        Spacer(modifier = Modifier.size(4.dp))

        Text(
            text = emailAddress,
            modifier = Modifier.wrapContentSize(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.size(4.dp))

        IconButton(
            modifier = Modifier.size(24.dp),
            onClick = onEditEmail,
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "edit email address button",
            )
        }
    }
}

@Composable
private fun GivenNameTextField(
    focusManager: AuthFormFocusManager,
    isEnabled: Boolean = true,
    text: String,
    onTextChange: (String) -> Unit = {},
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        placeholder = { Text(text = stringResource(id = R.string.given_names)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        enabled = isEnabled,
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "name input icon",
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .simpleFocusHandler(focusManager, PasswordAuthFormFocusField.GIVEN_NAMES),
    )
}

@Composable
private fun FamilyNameTextField(
    focusManager: AuthFormFocusManager,
    isEnabled: Boolean = true,
    text: String,
    onTextChange: (String) -> Unit = {},
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        placeholder = { Text(text = stringResource(id = R.string.family_name)) },
        enabled = isEnabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions { focusManager.clearFocus() },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .simpleFocusHandler(focusManager, PasswordAuthFormFocusField.FAMILY_NAME),
    )
}

@Composable
private fun PasswordTextField(
    focusManager: AuthFormFocusManager,
    isEnabled: Boolean = true,
    password: String,
    onPasswordChange: (String) -> Unit,
    imeAction: ImeAction,
    onImeAction: () -> Unit,
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    TextField(
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
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .simpleFocusHandler(focusManager, PasswordAuthFormFocusField.PASSWORD) { focusState ->
                if (!focusState.isFocused) {
                    isPasswordVisible = false
                }
            },
    )
}

@Preview
@Composable
private fun PasswordPreview() {
    PasswordTextField(
        focusManager = SimpleFocusManager(emptyArray()),
        password = "password",
        onPasswordChange = {},
        imeAction = ImeAction.Done,
        onImeAction = {},
    )
}

@Composable
private fun SubmitButton(
    pageState: PasswordAuthPageState,
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        enabled = isEnabled,
        onClick = onClick,
        modifier = Modifier.wrapContentSize(),
    ) {
        Text(
            text = pageState.submitButtonText(),
            textAlign = TextAlign.Center,
        )

        if (isLoading) {
            Spacer(modifier = Modifier.size(8.dp))
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    }
}

@Preview
@Composable
private fun SubmitFABPreview() {
    SubmitButton(
        PasswordAuthPage.SignUp.state(),
        onClick = {},
    )
}

@Composable
private fun PageToggleButton(
    pageState: PasswordAuthPageState,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier.wrapContentSize(),
    ) {
        Text(text = pageState.pageToggleHint())
    }
}

@Preview
@Composable
private fun PageToggleButtonPreview() {
    PageToggleButton(
        pageState = PasswordAuthPage.SignUp.state(),
        onClick = {},
    )
}
