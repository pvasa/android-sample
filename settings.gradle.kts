pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "Sample"

include(":androidApp")
include(":androidApp:ui")

// shared
include(":ui")
include(":data")
include(":domain")
