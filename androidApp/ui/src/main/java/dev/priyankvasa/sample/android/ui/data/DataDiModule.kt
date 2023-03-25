package dev.priyankvasa.sample.android.ui.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dev.priyankvasa.sample.data.Di
import dev.priyankvasa.sample.data.sample.repository.SampleRepository
import dev.priyankvasa.sample.data.user.UserRepository

@Module
@InstallIn(ViewModelComponent::class)
object DataDiModule {

    @ViewModelScoped
    @Provides
    fun provideSampleRepository(): SampleRepository = Di.get()

    @ViewModelScoped
    @Provides
    fun provideUserRepository(): UserRepository = Di.get()
}
