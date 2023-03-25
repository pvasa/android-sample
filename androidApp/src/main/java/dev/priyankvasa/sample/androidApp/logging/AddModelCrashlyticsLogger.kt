package dev.priyankvasa.sample.androidApp.logging

import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.priyankvasa.sample.data.logging.addLogger

fun addDataCrashlyticsLogger() {
    addLogger(
        performLog = { _, throwable, message ->
            with(FirebaseCrashlytics.getInstance()) {
                message?.let(::log)
                throwable?.let(::recordException)
            }
        },
    )
}
