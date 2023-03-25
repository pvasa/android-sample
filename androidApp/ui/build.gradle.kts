plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    `codeformatting-conventions`
}

group = "${Config.Android.group}.ui"

android {
    namespace = group as String
    compileSdk = Config.Android.sdk

    defaultConfig {
        minSdk = Config.Android.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    kotlinOptions {
        jvmTarget = Config.Jvm.version.toString()
    }

    buildFeatures {
        compose = true
        viewBinding = true

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

    /*tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }*/

    libraryVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(fileTree("include" to arrayOf("*.jar"), "dir" to "libs"))
    implementation(project(":ui"))

    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(kotlin("stdlib"))

    // /////// Compose /////////
    implementation(platform("androidx.compose:compose-bom:${DependencyVersions.composeBom}"))
    implementation("androidx.compose.ui:ui")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation")
    // Material Design
    implementation("androidx.compose.material3:material3")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // navigation
    implementation("androidx.navigation:navigation-compose:${DependencyVersions.navigationCompose}")
    implementation("androidx.hilt:hilt-navigation-compose:${DependencyVersions.hiltNavigationCompose}")
    implementation("com.google.accompanist:accompanist-navigation-animation:${DependencyVersions.composeAccompanist}")
    implementation("com.google.accompanist:accompanist-systemuicontroller:${DependencyVersions.composeAccompanist}")

    implementation("androidx.constraintlayout:constraintlayout-compose:${DependencyVersions.constraintLayoutCompose}")

    // Image loading compose
    implementation("io.coil-kt:coil-compose:${DependencyVersions.coil}")

    // accompanist
    implementation("com.google.accompanist:accompanist-swiperefresh:${DependencyVersions.composeAccompanist}")
    implementation("com.google.accompanist:accompanist-placeholder-material:${DependencyVersions.composeAccompanist}")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    // /////////////////////////

    // Ui and support
    implementation("androidx.appcompat:appcompat:${DependencyVersions.appcompat}")

    // Collection
    implementation("androidx.collection:collection-ktx:${DependencyVersions.collection}")
    // Logging
    implementation("com.jakewharton.timber:timber:${DependencyVersions.timber}")
    // Dependency injection
    implementation("com.google.dagger:hilt-android:${DependencyVersions.hilt}")
    kapt("com.google.dagger:hilt-compiler:${DependencyVersions.hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${DependencyVersions.hilt}")

    // Background handling
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${DependencyVersions.coroutines}")
    implementation("androidx.work:work-runtime-ktx:${DependencyVersions.work}")
    // Ktx
    implementation("androidx.core:core-ktx:${DependencyVersions.ktx}")
    implementation("androidx.activity:activity-ktx:${DependencyVersions.ktxActivity}")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:${DependencyVersions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${DependencyVersions.lifecycle}")

    // Firebase
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:${DependencyVersions.firebaseBom}"))

    // implementation(Config.Libs.firebaseCore)
    // implementation(Config.Libs.firebaseAuth)
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${DependencyVersions.kotlinxSerialization}")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:${DependencyVersions.kotlinxDateTime}")

    // Charts
//    implementation("com.github.PhilJay:MPAndroidChart:${DependencyVersions.mpAndroidChart}")
//    implementation("com.diogobernardino:williamchart:3.10.1")
    implementation("com.himanshoe:charty:1.0.1")

    // Unit testing
    testImplementation(kotlin("test"))

    // Ui testing
    androidTestImplementation("androidx.arch.core:core-testing:${DependencyVersions.coreTesting}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${DependencyVersions.espresso}")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:${DependencyVersions.espresso}")
    androidTestImplementation("androidx.test.espresso:espresso-intents:${DependencyVersions.espresso}")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:${DependencyVersions.uiAutomator}")
}
