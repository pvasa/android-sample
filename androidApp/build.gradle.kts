plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
    id("com.google.firebase.firebase-perf")
    `codeformatting-conventions`
    `install-git-hooks`
}

group = "${Config.Project.group}.android"

android {
    val signingConfigRelease = signingConfigs.create("release") {
        storeFile = file("$projectDir/signing/release/keystore.jks")
        storePassword = System.getenv("ANDROID_SAMPLE_RELEASE_PASS")
        keyAlias = "alias"
        keyPassword = System.getenv("ANDROID_SAMPLE_RELEASE_PASS")
    }

    val signingConfigStage = signingConfigs.create("stage") {
        storeFile = file("$projectDir/signing/stage/keystore.jks")
        storePassword = System.getenv("ANDROID_SAMPLE_STAGE_PASS")
        keyAlias = "alias"
        keyPassword = System.getenv("ANDROID_SAMPLE_STAGE_PASS")
    }

    compileSdk = Config.Android.sdk

    defaultConfig {
        applicationId = "dev.priyankvasa.sample.android"
        minSdk = Config.Android.minSdk
        targetSdk = Config.Android.sdk
        versionCode = Config.Project.versionCode
        versionName = Config.Project.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
    }

    buildTypes {
        val debug by getting {
            isDebuggable = true
            isJniDebuggable = true
            applicationIdSuffix = ".debug"

            /*FirebasePerformance {
                instrumentationEnabled = false
            }*/
        }

        val nonDebugCommonConfig: com.android.build.api.dsl.ApplicationBuildType.() -> Unit = {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        val release by getting {
            nonDebugCommonConfig()
            signingConfig = signingConfigRelease
        }

        create("stage") {
            initWith(debug)
            nonDebugCommonConfig()
            applicationIdSuffix = ".stage"
            signingConfig = signingConfigStage
        }
    }

    compileOptions {
        sourceCompatibility = Config.Jvm.version
        targetCompatibility = Config.Jvm.version
    }

    kotlinOptions {
        jvmTarget = Config.Jvm.version.toString()
    }

    buildFeatures {
        // Disable unused AGP features
        dataBinding = false
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = DependencyVersions.compose
    }

    packagingOptions {
        resources.excludes += "/META-INF/AL2.0"
        resources.excludes += "/META-INF/LGPL2.1"
    }
    namespace = group as String

    /*tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }*/
}

kapt {
    correctErrorTypes = true
}

repositories {
    mavenCentral()
    google()
    maven { setUrl("https://jitpack.io") }
}

dependencies {
    implementation(project(":androidApp:ui"))
    // Project
    implementation(project(":data"))
    implementation(project(":domain"))
    // /////////////////////////

    // Local
    implementation(fileTree("include" to arrayOf("*.jar"), "dir" to "libs"))
    // /////////////////////////

    // Kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))
    // /////////////////////////

    // Logging
    implementation("com.jakewharton.timber:timber:${DependencyVersions.timber}")
    // /////////////////////////

    // Dependency injection
    implementation("com.google.dagger:hilt-android:${DependencyVersions.hilt}")
    kapt("com.google.dagger:hilt-compiler:${DependencyVersions.hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${DependencyVersions.hilt}")
    // /////////////////////////

    // Background handling
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${DependencyVersions.coroutines}")
    implementation("androidx.work:work-runtime-ktx:${DependencyVersions.work}")
    // /////////////////////////

    // Ktx
    implementation("androidx.core:core-ktx:${DependencyVersions.ktx}")
    // /////////////////////////

    // Firebase
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:${DependencyVersions.firebaseBom}"))

    // implementation(Config.Libs.firebaseCore)
    // implementation(Config.Libs.firebaseAuth)
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")
    // /////////////////////////

    // Unit Tests
    testImplementation(kotlin("test"))
    // /////////////////////////

    // UI Tests
    // /////////////////////////
}
