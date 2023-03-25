package dev.priyankvasa.sample.android.ui.authentication.password

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import dev.priyankvasa.sample.android.ui.R

enum class PasswordAuthPage {
    SignIn,
    SignUp,
}

data class PasswordAuthPageState(
    val page: PasswordAuthPage,
    val passwordFieldImeAction: ImeAction,
    val submitButtonText: @Composable () -> String,
    val pageHint: @Composable () -> String,
    val pageToggleHint: @Composable () -> String,
)

fun PasswordAuthPage.state(): PasswordAuthPageState =
    when (this) {
        PasswordAuthPage.SignIn -> PasswordAuthPageState(
            page = PasswordAuthPage.SignIn,
            passwordFieldImeAction = ImeAction.Send,
            submitButtonText = { stringResource(id = R.string.sign_in) },
            pageHint = { stringResource(id = R.string.sign_in_hint) },
            pageToggleHint = { stringResource(id = R.string.toggle_sign_up) },
        )

        PasswordAuthPage.SignUp -> PasswordAuthPageState(
            page = PasswordAuthPage.SignUp,
            passwordFieldImeAction = ImeAction.Next,
            submitButtonText = { stringResource(id = R.string.sign_up) },
            pageHint = { stringResource(id = R.string.sign_up_hint) },
            pageToggleHint = { stringResource(id = R.string.toggle_sign_in) },
        )
    }
