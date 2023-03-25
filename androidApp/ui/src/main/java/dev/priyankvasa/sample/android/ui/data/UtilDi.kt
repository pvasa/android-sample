package dev.priyankvasa.sample.android.ui.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dev.priyankvasa.sample.data.core.coroutines.CoroutineDispatcherProvider
import dev.priyankvasa.sample.data.util.EmailValidator

@Module
@InstallIn(ViewModelComponent::class)
object UtilDi {
    @ViewModelScoped
    @Provides
    fun provideEmailValidator(): EmailValidator = EmailValidator()

    @Provides
    fun provideCoroutineDispatcherProvider(): CoroutineDispatcherProvider =
        CoroutineDispatcherProvider.Default
}
