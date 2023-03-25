package dev.priyankvasa.sample.android.ui.authentication.password

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface UserAuthenticationState : Parcelable

@Parcelize
object UserNotAuthenticated : UserAuthenticationState

@Parcelize
object UserAuthenticated : UserAuthenticationState
