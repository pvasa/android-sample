package dev.priyankvasa.sample.androidApp.appBuild

import dev.priyankvasa.sample.androidApp.environment.Environment

sealed class BuildType(val name: String) {
    object Debug : BuildType(name = "debug")

    object Stage : BuildType(name = "stage")

    object Release : BuildType(name = "release")

    class Unknown(name: String) : BuildType(name)

    companion object {
        operator fun invoke(name: String): BuildType =
            when (name) {
                Debug.name -> Debug
                Stage.name -> Stage
                Release.name -> Release
                else -> Unknown(name)
            }
    }
}

fun BuildType.isProductionBuild(): Boolean = this == BuildType.Release

fun BuildType.isDebugBuild(): Boolean = this == BuildType.Debug

fun BuildType.isStageBuild(): Boolean = this == BuildType.Stage

fun BuildType.defaultEnv(): Environment =
    when (this) {
        BuildType.Debug -> Environment.Dev
        BuildType.Release -> Environment.Prod
        BuildType.Stage -> Environment.Int
        is BuildType.Unknown -> Environment.Stg
    }
