package dev.priyankvasa.sample.androidApp.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

object CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        with(FirebaseCrashlytics.getInstance()) {
            when (priority) {
                Log.ASSERT,
                Log.ERROR,
                Log.WARN,
                -> {
                    tag?.let(::log)
                    log(message)
                    t?.let(::recordException)
                }
                else -> return
            }
        }
    }
}
