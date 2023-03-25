package dev.priyankvasa.sample.data

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

object Di {
    @PublishedApi
    internal val dependencies = object : KoinComponent {}

    inline fun <reified T> get(): T = dev.priyankvasa.sample.data.Di.dependencies.get()
    inline fun <reified T> inject(): Lazy<T> = dev.priyankvasa.sample.data.Di.dependencies.inject()
}
