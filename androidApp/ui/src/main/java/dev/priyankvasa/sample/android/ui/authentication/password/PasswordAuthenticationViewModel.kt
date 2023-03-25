package dev.priyankvasa.sample.android.ui.authentication.password

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.priyankvasa.sample.android.ui.authentication.Authentication
import dev.priyankvasa.sample.android.ui.core.model.TaskState
import dev.priyankvasa.sample.android.ui.util.launchSafe
import dev.priyankvasa.sample.data.user.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PasswordAuthenticationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val emailAddress: String =
        requireNotNull(savedStateHandle[Authentication.Password.Args.emailAddress])

    val navRouteOnAuth: String =
        requireNotNull<String>(savedStateHandle[Authentication.Companion.Args.navRouteOnAuth])
            .let(Uri::decode)

    private val _userAuthenticationState =
        MutableStateFlow<UserAuthenticationState>(UserNotAuthenticated)
    val userAuthenticationState = _userAuthenticationState.asStateFlow()

    private val pageMutable: MutableStateFlow<PasswordAuthPage> =
        MutableStateFlow(PasswordAuthPage.SignIn)
    val pageState: StateFlow<PasswordAuthPageState> =
        pageMutable.mapLatest { it.state() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                initialValue = PasswordAuthPage.SignIn.state(),
            )

    private val _authenticationState: MutableStateFlow<TaskState> =
        MutableStateFlow(TaskState.Idle)
    val authenticationState: StateFlow<TaskState> =
        _authenticationState.asStateFlow()

    fun dismissAuthenticationFailure() {
        _authenticationState.value = TaskState.Idle
    }

    fun submit(emailAddress: String, familyName: String, givenName: String, password: String) {
        when (pageMutable.value) {
            PasswordAuthPage.SignIn -> signIn(emailAddress, password)
            PasswordAuthPage.SignUp -> signUp(emailAddress, familyName, givenName, password)
        }
    }

    private fun signIn(emailAddress: String, password: String) {
        if (emailAddress.isBlank() || password.isBlank()) {
            _authenticationState.value = TaskState.Failed(PasswordAuthError.EmptyField)
        } else {
            _authenticationState.value = TaskState.Running
            viewModelScope.launchSafe {
                userRepository.signInUser(emailAddress, password)
                    .onSuccess {
                        _authenticationState.value = TaskState.Idle
                        _userAuthenticationState.value = UserAuthenticated
                    }
                    .onFailure {
                        _authenticationState.value = TaskState.Failed(it)
                    }
            }
        }
    }

    private fun signUp(
        emailAddress: String,
        familyName: String,
        givenName: String,
        password: String,
    ) {
        if (emailAddress.isBlank() ||
            familyName.isBlank() ||
            givenName.isBlank() ||
            password.isBlank()
        ) {
            _authenticationState.value = TaskState.Failed(PasswordAuthError.EmptyField)
        } else {
            viewModelScope.launchSafe {
                _authenticationState.value = TaskState.Running

                userRepository.signupUser(
                    emailAddress,
                    familyName,
                    givenName,
                    password,
                )
                    .onSuccess {
                        _authenticationState.value = TaskState.Idle
                        pageMutable.value = PasswordAuthPage.SignIn
                    }
                    .onFailure {
                        _authenticationState.value = TaskState.Failed(it)
                    }
            }
        }
    }

    fun togglePage() {
        _authenticationState.value = TaskState.Idle

        pageMutable.value = when (pageMutable.value) {
            PasswordAuthPage.SignIn -> PasswordAuthPage.SignUp
            PasswordAuthPage.SignUp -> PasswordAuthPage.SignIn
        }
    }
}
