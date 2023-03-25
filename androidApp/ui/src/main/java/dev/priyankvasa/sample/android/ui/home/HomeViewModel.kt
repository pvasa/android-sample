package dev.priyankvasa.sample.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.priyankvasa.sample.android.ui.authentication.password.UserAuthenticated
import dev.priyankvasa.sample.android.ui.authentication.password.UserAuthenticationState
import dev.priyankvasa.sample.android.ui.authentication.password.UserNotAuthenticated
import dev.priyankvasa.sample.android.ui.core.model.TaskState
import dev.priyankvasa.sample.android.ui.util.launchSafe
import dev.priyankvasa.sample.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _signOutState = MutableStateFlow<TaskState>(TaskState.Idle)
    val signOutState = _signOutState.asStateFlow()

    private val _authenticationState =
        MutableStateFlow<UserAuthenticationState>(UserAuthenticated)
    val authenticationState = _authenticationState.asStateFlow()

    fun signOut() {
        viewModelScope.launchSafe {
            _signOutState.value = TaskState.Running
            userRepository.signOut()
            _signOutState.value = TaskState.Idle
            _authenticationState.value = UserNotAuthenticated
        }
    }
}
