package dev.priyankvasa.sample.androidApp

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import dev.priyankvasa.sample.androidApp.appBuild.BuildType
import dev.priyankvasa.sample.androidApp.appBuild.CurrentBuildType
import dev.priyankvasa.sample.androidApp.appBuild.defaultEnv
import dev.priyankvasa.sample.androidApp.appBuild.isDebugBuild
import dev.priyankvasa.sample.androidApp.appBuild.isProductionBuild
import dev.priyankvasa.sample.androidApp.logging.CrashlyticsTree
import dev.priyankvasa.sample.androidApp.logging.addDataCrashlyticsLogger
import dev.priyankvasa.sample.data.core.Data
import dev.priyankvasa.sample.data.logging.enableDebugLogging
import dev.priyankvasa.sample.domain.Domain
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SampleApplication : Application() {
    @Inject
    @CurrentBuildType
    lateinit var currentBuildType: BuildType

    override fun onCreate() {
        super.onCreate()
        initLogging()
        initModel()
    }

    private fun initModel() {
        Domain.init()
        Data.init {
            configureJson {
                prettyPrint = currentBuildType.isDebugBuild()
                coerceInputValues = !currentBuildType.isDebugBuild()
            }

            configureRemoteApi(
                config = Data.Config.RemoteApiConfig(
                    baseUrl = currentBuildType.defaultEnv().serverUrl,
                    logHeaders = !currentBuildType.isProductionBuild(),
                    logBody = !currentBuildType.isProductionBuild(),
                    logInfo = !currentBuildType.isProductionBuild(),
                ),
            )
        }
    }

    private fun initLogging() {
        if (currentBuildType.isProductionBuild()) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            addDataCrashlyticsLogger()
            Timber.plant(CrashlyticsTree)
        } else {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
            enableDebugLogging()
            Timber.plant(Timber.DebugTree())
        }
    }
}
