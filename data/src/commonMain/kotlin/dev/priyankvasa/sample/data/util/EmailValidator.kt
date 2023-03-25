package dev.priyankvasa.sample.data.util

class EmailValidator private constructor() {
    fun isValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matches(email)

    companion object {
        operator fun invoke(): EmailValidator = EmailValidator()
    }
}
