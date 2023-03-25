package dev.priyankvasa.sample.domain.core.util

import dev.priyankvasa.sample.domain.core.UseCase

typealias NoInputUseCase<R> = UseCase<Nothing?, R>

typealias NoOutputUseCase<P> = UseCase<P, Unit>

typealias NoInputOutputUseCase = UseCase<Nothing?, Unit>
