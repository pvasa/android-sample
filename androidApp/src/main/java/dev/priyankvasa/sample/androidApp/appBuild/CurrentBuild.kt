package dev.priyankvasa.sample.androidApp.appBuild

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.priyankvasa.sample.android.BuildConfig
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
internal object CurrentBuild {
    @Provides
    @CurrentBuildVersion
    fun buildVersion(): BuildVersion =
        BuildVersion(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

    @Provides
    @CurrentBuildType
    fun buildType(): BuildType =
        BuildType(BuildConfig.BUILD_TYPE)
}

@Qualifier
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.SOURCE)
internal annotation class CurrentBuildType

@Qualifier
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.SOURCE)
internal annotation class CurrentBuildVersion
