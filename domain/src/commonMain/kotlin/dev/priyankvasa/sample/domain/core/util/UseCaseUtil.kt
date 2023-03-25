package dev.priyankvasa.sample.domain.core.util

import dev.priyankvasa.sample.domain.core.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking

fun <P, R, F : Flow<R>> UseCase<P, F>.stream(
    params: P,
): F = runBlocking { invoke(params) }

suspend inline operator fun <R> NoInputUseCase<R>.invoke(): R = invoke(null)

fun <R, F : Flow<R>> NoInputUseCase<F>.stream(): F = stream(null)
