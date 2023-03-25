package dev.priyankvasa.sample.data.ktor.customfeatures

/*

import dev.priyankvasa.sample.data.core.model.NetworkError
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.save
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.json.JsonPlugin
import io.ktor.client.plugins.json.JsonSerializer
import io.ktor.client.plugins.plugin
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpResponseContainer
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.AttributeKey
import io.ktor.util.date.GMTDate
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.SharedImmutable
import kotlin.reflect.KClass
import kotlin.reflect.KType

@SharedImmutable
private val ValidateMark = AttributeKey<Unit>("ValidateMark")

internal class ResultPlugin {
    fun canProcess(info: TypeInfo): Boolean {
        val dataType = info.kotlinType?.arguments?.firstOrNull()

        return dataType != null &&
            info.type.qualifiedName == Result::class.qualifiedName
    }

    suspend fun transformResponse(
        response: HttpResponse,
        serializer: JsonSerializer,
        info: TypeInfo,
    ): Result<Any> =
        runAppTaskCatching {
            val statusCode = response.status.value
            val originCall = response.call
            if (statusCode < 300 || originCall.attributes.contains(ValidateMark)) {
                Result.success(
                    serializer.read(
                        getResponseType(info),
                        response.bodyAsChannel().readRemaining()
                    )
                )
            }

            val exceptionCall = originCall.save().apply {
                attributes.put(ValidateMark, Unit)
            }

            val exceptionResponse = exceptionCall.response
            val exceptionResponseText = exceptionResponse.bodyAsText()
            when (statusCode) {
                in 300..399 -> throw RedirectResponseException(
                    exceptionResponse,
                    exceptionResponseText
                )
                in 400..499 -> throw ClientRequestException(
                    exceptionResponse,
                    exceptionResponseText
                )
                in 500..599 -> throw ServerResponseException(
                    exceptionResponse,
                    exceptionResponseText
                )
                else -> throw ResponseException(exceptionResponse, exceptionResponseText)
            }
        }
            .getOrElse { error ->
                Result.failure(NetworkError(error))
            }

    private fun getResponseType(info: TypeInfo): TypeInfo =
        info.kotlinType!!.arguments.first()
            .let { dataType ->
                TypeInfo(
                    type = dataType.type?.classifier as KClass<*>,
                    reifiedType = dataType.type as KType,
                    kotlinType = dataType.type
                )
            }

    companion object Plugin : HttpClientPlugin<Unit, ResultPlugin> {
        override val key: AttributeKey<ResultPlugin> =
            AttributeKey("Result")

        override fun install(feature: ResultPlugin, scope: HttpClient) {
            val statusCodeKey =
                AttributeKey<HttpStatusCode>("HttpStatusCode")

            scope.receivePipeline.intercept(HttpReceivePipeline.Before) { httpResponse ->
                scope.attributes.put(statusCodeKey, httpResponse.status)
                proceedWith(httpResponse.copy(status = HttpStatusCode.OK))
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { (typeInfo, response) ->
                if (response !is HttpResponse ||
                    !feature.canProcess(typeInfo)
                ) {
                    proceed()
                    return@intercept
                }

                runAppTaskCatching {
                    proceedWith(
                        HttpResponseContainer(
                            typeInfo,
                            feature.transformResponse(
                                response,
                                scope.plugin(JsonPlugin).serializer,
                                typeInfo
                            )
                        )
                    )
                }
                    .onFailure { error ->
                        proceedWith(
                            HttpResponseContainer(
                                typeInfo,
                                Result.failure<Any>(NetworkError(error))
                            )
                        )
                    }
            }
        }

        override fun prepare(block: Unit.() -> Unit): ResultPlugin {
            return ResultPlugin()
        }
    }
}

internal fun HttpResponse.copy(
    call: HttpClientCall = this.call,
    content: ByteReadChannel = this.content,
    requestTime: GMTDate = this.requestTime,
    responseTime: GMTDate = this.responseTime,
    status: HttpStatusCode = this.status,
    version: HttpProtocolVersion = this.version,
    headers: Headers = this.headers,
    coroutineContext: CoroutineContext = this.coroutineContext,
) = object : HttpResponse() {
    override val call: HttpClientCall = call
    override val content: ByteReadChannel = content
    override val requestTime: GMTDate = requestTime
    override val responseTime: GMTDate = responseTime
    override val status: HttpStatusCode = status
    override val version: HttpProtocolVersion = version
    override val headers: Headers = headers
    override val coroutineContext: CoroutineContext = coroutineContext
}
*/
