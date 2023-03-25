package dev.priyankvasa.sample.domain

import dev.priyankvasa.sample.data.core.util.runAppTaskCatching
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

object Domain {
    fun init() {
        initKoin()
    }

    private fun initKoin() {
        runAppTaskCatching { startKoin {} }
        loadKoinModules(DomainDiModule)
    }
}
