package dev.priyankvasa.sample.data.core

import dev.priyankvasa.sample.data.core.model.RemoteApiConfig
import dev.priyankvasa.sample.data.core.util.runAppTaskCatching
import dev.priyankvasa.sample.data.dataDiModule
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import kotlin.native.concurrent.ThreadLocal

internal expect var internalConfig: Data.Config?
    private set

@ThreadLocal
object Data {
    private val defaultJsonBuilderConfig: JsonBuilder.() -> Unit = {
        prettyPrint = false
        ignoreUnknownKeys = true
        encodeDefaults = true
        coerceInputValues = true
    }

    private var internalJson: Json = Json {
        apply(defaultJsonBuilderConfig)
    }

    internal val json: Json get() = internalJson

    private val config: Config
        get() =
            if (internalConfig == null) {
                throw IllegalStateException("Model must be initialized! Please call `Model.init()` before anything else.")
            } else {
                internalConfig!!
            }

    fun init(func: Config.() -> Unit = {}) {
        if (internalConfig != null) {
            throw UnsupportedOperationException("Model is already initialized!")
        }
        internalConfig = Config().apply(func)

        val apiConfig = config.remoteApiConfig.run {
            RemoteApiConfig(baseUrl, logInfo, logHeaders, logBody, json)
        }

        initKoin(apiConfig)
    }

    private fun initKoin(apiConfig: RemoteApiConfig) {
        runAppTaskCatching { startKoin {} }
        loadKoinModules(dev.priyankvasa.sample.data.dataDiModule(apiConfig))
    }

    class Config {
        private lateinit var internalRemoteApiConfig: RemoteApiConfig
        internal val remoteApiConfig: RemoteApiConfig get() = internalRemoteApiConfig

        fun configureJson(config: JsonBuilder.() -> Unit) {
            internalJson = Json(internalJson) {
                apply(config)
            }
        }

        fun configureRemoteApi(
            config: RemoteApiConfig,
        ) {
            internalRemoteApiConfig = config
        }

        data class RemoteApiConfig(
            val baseUrl: String,
            val logInfo: Boolean,
            val logHeaders: Boolean,
            val logBody: Boolean,
        )
    }
}
