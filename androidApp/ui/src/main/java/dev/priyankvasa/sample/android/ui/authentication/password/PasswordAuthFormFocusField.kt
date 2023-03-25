package dev.priyankvasa.sample.android.ui.authentication.password

import dev.priyankvasa.sample.android.ui.util.FocusField

enum class PasswordAuthFormFocusField : FocusField {
    FAMILY_NAME,

    GIVEN_NAMES {
        override val next: FocusField = FAMILY_NAME
    },

    PASSWORD {
        override val next: FocusField = GIVEN_NAMES
    },
}
