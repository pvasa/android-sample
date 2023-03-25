plugins {
    id("com.android.application") version DependencyVersions.androidBuildTools apply false
    id("com.android.library") version DependencyVersions.androidBuildTools apply false
    kotlin("android") version DependencyVersions.kotlin apply false
    kotlin("multiplatform") version DependencyVersions.kotlin apply false
    kotlin("plugin.serialization") version DependencyVersions.kotlin apply false
    id("com.google.devtools.ksp") version DependencyVersions.ksp apply false
    `codeformatting-conventions`
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", DependencyVersions.kotlin))
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.4")
        classpath("com.google.firebase:perf-plugin:1.4.2")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${DependencyVersions.hilt}")
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${DependencyVersions.atomicfu}")
    }
}

group = Config.Project.group
version = Config.Project.versionName

val clean = tasks.getByName<Delete>("clean") {
    delete(rootProject.buildDir)
}

listOf(
    "preBuild",
    "compileKotlin",
    "compileJava",
    "compileGroovy",
    "compile",
    "assembleDebug",
    "assemble",
).forEach { task ->
    tasks.findByName(task)?.mustRunAfter(clean)
}
