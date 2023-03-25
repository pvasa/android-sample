package dev.priyankvasa.sample.android.ui.authentication.methods

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.priyankvasa.sample.android.ui.authentication.Authentication
import dev.priyankvasa.sample.data.util.EmailValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthenticationMethodsViewModel @Inject constructor(
    emailValidator: EmailValidator,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val navRouteOnAuth: String =
        requireNotNull<String>(savedStateHandle[Authentication.Companion.Args.navRouteOnAuth])
            .let(Uri::decode)

    private val _emailAddress = MutableStateFlow("")
    val emailAddress = _emailAddress.asStateFlow()

    val canContinueWithEmail =
        emailAddress.map(emailValidator::isValid)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                initialValue = false,
            )

    fun updateEmailAddress(email: String) {
        _emailAddress.value = email
    }
}
