package dev.priyankvasa.sample.data.sample.datasource.remote

interface SampleService {

    suspend fun getSample(): String
}
