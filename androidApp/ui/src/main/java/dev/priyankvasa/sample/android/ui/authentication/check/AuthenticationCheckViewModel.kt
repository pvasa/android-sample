package dev.priyankvasa.sample.android.ui.authentication.check

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.priyankvasa.sample.android.ui.authentication.Authentication
import dev.priyankvasa.sample.android.ui.core.model.TaskState
import dev.priyankvasa.sample.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthenticationCheckViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val navRouteOnAuth: String =
        requireNotNull<String>(savedStateHandle[Authentication.Companion.Args.navRouteOnAuth])
            .let(Uri::decode)

    private val _userAuthCheckState = MutableStateFlow<TaskState>(TaskState.Idle)
    val userAuthCheckState = _userAuthCheckState.asStateFlow()

    val navigateOnAuth: StateFlow<Result<Unit>?> = flow {
        _userAuthCheckState.emit(TaskState.Running)
        emit(userRepository.refreshAuthSession())
        _userAuthCheckState.emit(TaskState.Idle)
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = null)
}
