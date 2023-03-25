package dev.priyankvasa.sample.data.logging

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier

fun addLogger(
    isEnable: (priority: LoggerLevel, tag: String?) -> Boolean = { _, _ -> true },
    performLog: (
        tag: String?,
        throwable: Throwable?,
        message: String?,
    ) -> Unit,
) {
    val logger = object : Antilog() {
        override fun isEnable(priority: LogLevel, tag: String?): Boolean {
            val level = when (priority) {
                LogLevel.VERBOSE -> LoggerLevel.VERBOSE
                LogLevel.DEBUG -> LoggerLevel.DEBUG
                LogLevel.INFO -> LoggerLevel.INFO
                LogLevel.WARNING -> LoggerLevel.WARNING
                LogLevel.ERROR -> LoggerLevel.ERROR
                LogLevel.ASSERT -> LoggerLevel.ASSERT
            }

            return isEnable(level, tag)
        }

        override fun performLog(
            priority: LogLevel,
            tag: String?,
            throwable: Throwable?,
            message: String?,
        ) {
            performLog(tag, throwable, message)
        }
    }

    Napier.base(logger)
}
