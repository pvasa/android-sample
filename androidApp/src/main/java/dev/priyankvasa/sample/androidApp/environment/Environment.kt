package dev.priyankvasa.sample.androidApp.environment

sealed interface Environment {
    val id: String
        get() = this::class.java.simpleName
            .lowercase()

    val use: String

    val displayName: String get() = "$id ($use)"

    val serverUrl: String
        get() = "https://$id.api.sample.priyankvasa.dev"

    object Int : Environment {
        override val use = "Integration"
    }

    object Stg : Environment {
        override val use = "Staging"
    }

    object Dev : Environment {
        override val use = "Development"
        override val serverUrl: String = "https://localhost:5443"
    }

    object Dr : Environment {
        override val use = "Data recovery"
    }

    object Hfix : Environment {
        override val use = "Hotfix"
    }

    object Demo : Environment {
        override val use = "Demo"
    }

    object Prod : Environment {
        override val use = "Production"
    }

    data class Other(
        override val use: String,
        override val id: String,
        override val serverUrl: String,
    ) : Environment

    companion object
}
