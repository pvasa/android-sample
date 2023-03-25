package dev.priyankvasa.sample.android.ui.authentication.password

sealed class PasswordAuthError : Exception() {
    object EmptyField : PasswordAuthError()
}
