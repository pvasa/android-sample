package dev.priyankvasa.sample.android.ui.authentication.methods

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.priyankvasa.sample.android.ui.LocalNavController
import dev.priyankvasa.sample.android.ui.R
import dev.priyankvasa.sample.android.ui.authentication.Authentication
import dev.priyankvasa.sample.android.ui.authentication.password.EmailTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationMethods(viewModel: AuthenticationMethodsViewModel) {
    val navController = LocalNavController.current

    val emailAddress by viewModel.emailAddress.collectAsStateWithLifecycle()
    val canContinueWithEmail by viewModel.canContinueWithEmail.collectAsStateWithLifecycle()

    fun navigateToPasswordInput() {
        navController.navigate(
            Authentication.Password(
                emailAddress,
                viewModel.navRouteOnAuth,
            ),
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { parentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(parentPadding)
                .padding(vertical = 24.dp, horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(id = R.string.welcome_to_sample),
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.size(24.dp))

            EmailTextField(
                Modifier,
                emailAddress,
                onEmailChange = viewModel::updateEmailAddress,
                imeAction = if (canContinueWithEmail) ImeAction.Go else ImeAction.None,
                onImeAction = ::navigateToPasswordInput,
            )

            Spacer(modifier = Modifier.size(8.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                enabled = canContinueWithEmail,
                onClick = ::navigateToPasswordInput,
            ) {
                Text(text = stringResource(id = R.string.continue_with_email))
            }
        }
    }
}
