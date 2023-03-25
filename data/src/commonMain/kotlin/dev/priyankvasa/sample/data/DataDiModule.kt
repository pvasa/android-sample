package dev.priyankvasa.sample.data

import com.russhwolf.settings.Settings
import dev.priyankvasa.sample.data.auth.AuthService
import dev.priyankvasa.sample.data.core.coroutines.CoroutineDispatcherProvider
import dev.priyankvasa.sample.data.core.model.RemoteApiConfig
import dev.priyankvasa.sample.data.sample.datasource.remote.SampleService
import dev.priyankvasa.sample.data.sample.datasource.remote.SampleServiceImpl
import dev.priyankvasa.sample.data.sample.repository.SampleRepository
import dev.priyankvasa.sample.data.user.UserRepository
import org.koin.dsl.binds
import org.koin.dsl.module

internal fun dataDiModule(
    apiConfig: RemoteApiConfig,
) = module {
    single { Settings() }

    single {
        SampleServiceImpl(config = apiConfig, settings = get())
    } binds arrayOf(SampleService::class, AuthService::class)

    single<CoroutineDispatcherProvider> {
        CoroutineDispatcherProvider.Default
    }

    factory {
        SampleRepository(
            sampleService = get(),
            dispatcherProvider = get(),
        )
    }

    factory {
        UserRepository(
            authService = get(),
            settings = get(),
            dispatcherProvider = get(),
        )
    }
}
