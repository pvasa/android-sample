import org.gradle.api.JavaVersion

object Config {
    object Project {
        private const val majorVersion = 1
        private const val minorVersion = 0
        private const val patchVersion = 0
        private const val versionClassifier = "SNAPSHOT"

        const val group: String = "dev.priyankvasa.sample"

        val versionName: String
            get() = "$majorVersion.$minorVersion.$patchVersion${if (versionClassifier.isNotBlank()) "-" else ""}$versionClassifier"

        val versionCode: Int
            get() = Android.minSdk * 10000000 +
                majorVersion * 10000 +
                minorVersion * 100 +
                patchVersion
    }

    object ProjectProperties {
        const val codeFormattingEnabled = "codeFormattingEnabled"
        const val installGitHooks = "installGitHooks"
    }

    object Jvm {
        val version = JavaVersion.VERSION_11
    }

    object Android {
        const val group = "${Project.group}.android"
        const val minSdk = 21
        const val sdk = 33
    }

    object IOS {
        const val deploymentTarget = "14.2"
    }
}
