package dev.priyankvasa.sample.data.sample.datasource.remote

import com.russhwolf.settings.Settings
import dev.priyankvasa.sample.data.core.model.RemoteApiConfig
import dev.priyankvasa.sample.data.core.remote.HttpClient
import dev.priyankvasa.sample.data.core.remote.rest.RestApi
import dev.priyankvasa.sample.data.core.remote.stream.StreamApi
import dev.priyankvasa.sample.data.core.remote.stream.StreamApiDefault
import dev.priyankvasa.sample.data.ktor.customfeatures.Authorization

internal class SampleServiceImpl private constructor(
    restApi: RestApi,
    streamApi: StreamApi,
    authTokensManager: dev.priyankvasa.sample.data.auth.AuthTokensManager,
) : SampleService,
    dev.priyankvasa.sample.data.auth.AuthService by dev.priyankvasa.sample.data.auth.AuthServiceImpl(
        restApi,
        authTokensManager,
    ) {
    override suspend fun getSample(): String = "Sample response"

    companion object {
        operator fun invoke(
            config: RemoteApiConfig,
            settings: Settings,
        ): SampleServiceImpl {
            val httpClient = HttpClient(config)

            val authTokensManager = dev.priyankvasa.sample.data.auth.SampleServiceAuthTokensManagerImpl(
                settings,
                RestApi(httpClient),
            )

            val authHttpClient = httpClient.config {
                install(Authorization) {
                    authTokensProvider = authTokensManager
                }
            }

            return SampleServiceImpl(
                RestApi(authHttpClient),
                StreamApiDefault(httpClient),
                authTokensManager,
            )
        }
    }
}
