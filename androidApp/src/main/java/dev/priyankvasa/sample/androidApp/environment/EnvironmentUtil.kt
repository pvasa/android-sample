package dev.priyankvasa.sample.androidApp.environment

private val SERVER_URL_TO_ENV_MAP: Map<String, Environment> =
    Environment::class.sealedSubclasses
        .mapNotNull { envClass -> envClass.objectInstance }
        .associateBy { env -> env.serverUrl }

val Environment.Companion.ALL: Collection<Environment>
    get() = SERVER_URL_TO_ENV_MAP.values

fun Environment.Companion.findByServerUrl(serverUrl: String): Environment? =
    SERVER_URL_TO_ENV_MAP[serverUrl]
