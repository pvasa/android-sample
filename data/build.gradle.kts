plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("kotlinx-atomicfu")
    `codeformatting-conventions`
}

group = "${Config.Project.group}.data"
version = Config.Project.versionName

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = Config.IOS.deploymentTarget
        framework {
            baseName = "data"
        }
        podfile = rootProject.file("iosApp/Podfile")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("reflect"))

                // Threading
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependencyVersions.coroutines}")

                // Serialization
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:${DependencyVersions.kotlinxSerialization}")

                // Networking
                implementation("io.ktor:ktor-client-core:${DependencyVersions.ktor}")
                implementation("io.ktor:ktor-client-auth:${DependencyVersions.ktor}")
                implementation("io.ktor:ktor-client-json:${DependencyVersions.ktor}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${DependencyVersions.ktor}")
                implementation("io.ktor:ktor-client-content-negotiation:${DependencyVersions.ktor}")
                implementation("io.ktor:ktor-client-logging:${DependencyVersions.ktor}")

                // date-time
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${DependencyVersions.kotlinxDateTime}")

                // Logging
                implementation("io.github.aakira:napier:${DependencyVersions.napier}")

                // Settings - shared prefs/user defaults
                implementation("com.russhwolf:multiplatform-settings:${DependencyVersions.multiplatformSettings}")
                implementation("com.russhwolf:multiplatform-settings-no-arg:${DependencyVersions.multiplatformSettings}")

                implementation("org.jetbrains.kotlinx:atomicfu:${DependencyVersions.atomicfu}")

                // DI
                implementation("io.insert-koin:koin-core:${DependencyVersions.koin}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.insert-koin:koin-test:${DependencyVersions.koin}")
            }
        }
        val androidMain by getting {
            dependencies {
                // Networking
                implementation("io.ktor:ktor-client-android:${DependencyVersions.ktor}")
                implementation("io.ktor:ktor-client-okhttp:${DependencyVersions.ktor}")
                implementation("io.ktor:ktor-client-json-jvm:${DependencyVersions.ktor}")
                implementation("io.ktor:ktor-client-serialization-jvm:${DependencyVersions.ktor}")
                implementation("io.ktor:ktor-client-logging-jvm:${DependencyVersions.ktor}")

                // ktx
                implementation("androidx.core:core-ktx:${DependencyVersions.ktx}")
            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                // Networking
                implementation("io.ktor:ktor-client-darwin:${DependencyVersions.ktor}")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "$group.android"
    compileSdk = Config.Android.sdk

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = Config.Android.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}
