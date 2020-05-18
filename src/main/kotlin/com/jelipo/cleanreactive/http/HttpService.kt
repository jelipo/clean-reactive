package com.jelipo.cleanreactive.http

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.jelipo.cleanreactive.http.HttpBuilder.HttpRequest
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

/**
 * 发送Http请求的Service
 *
 * @author Jelipo
 * @date 2020/05/18 21:03
 */
class HttpService(private val objectMapper: ObjectMapper, builder: WebClient.Builder) {
    private val webClient: WebClient

    /**
     * 发送同步的HTTP请求
     *
     * @param clazz 目前仅支持 [String]、[Map] 和 实体Pojo类(结果必须为JSON)
     * @return 序列化完成的实体
     */

    fun <T> syncRequest(httpRequest: HttpRequest, clazz: Class<T>): T {
        val resultMono = send(httpRequest)
        return jsonSerialize(resultMono.block(), clazz)
    }

    /**
     * 发送同步的HTTP请求
     *
     * @param typeReference 目前仅支持 实体Pojo类(结果必须为JSON)
     * @return 序列化完成的实体
     */

    fun <T> syncRequest(httpRequest: HttpRequest, typeReference: TypeReference<T>): T {
        return jsonSerialize(send(httpRequest).block(), typeReference)
    }

    /**
     * 发送异步的HTTP请求
     *
     * @param clazz 目前仅支持 [String] 和 实体Pojo类(结果必须为JSON)
     * @return 异步结果的 CompletableFuture
     */

    fun <T> asyncRequest(httpRequest: HttpRequest, clazz: Class<T>): CompletableFuture<T> {
        val resultMono = send(httpRequest)
        return CompletableFuture.supplyAsync { jsonSerialize(resultMono.block(), clazz) }
    }

    /**
     * 发送异步的HTTP请求
     *
     * @param type 目前仅支持 实体Pojo类(结果必须为JSON)
     * @return 异步结果的 CompletableFuture
     */

    fun <T> asyncRequest(httpRequest: HttpRequest, type: TypeReference<T>): CompletableFuture<T> {
        val resultMono = send(httpRequest)
        return CompletableFuture.supplyAsync { jsonSerialize(resultMono.block(), type) }
    }

    /**
     * 发送异步的HTTP请求，返回值为Mono
     *
     * @param clazz 目前仅支持 [String] 和 实体Pojo类(结果必须为JSON)
     * @return Mono
     */

    fun <T> monoRequest(httpRequest: HttpRequest, clazz: Class<T>): Mono<T> {
        val resultByteMono = send(httpRequest)
        return resultByteMono.map { jsonSerialize(it, clazz) }
    }

    /**
     * 发送异步的HTTP请求，返回值为Mono
     *
     * @param type 目前仅支持 实体Pojo类(结果必须为JSON)
     * @return 异步结果的 CompletableFuture
     */
    fun <T> monoRequest(httpRequest: HttpRequest, type: TypeReference<T>): Mono<T> {
        return send(httpRequest).map { it: ByteArray? -> jsonSerialize(it, type) }
    }

    private fun send(httpRequest: HttpRequest): Mono<ByteArray> {
        return try {
            val body = httpRequest.body
            logger.info(httpRequest.url + if (body == null) "" else ":$body")
            send(httpRequest.url, null, httpRequest.httpMethod, httpRequest.getMediaType(),
                    httpRequest.body, httpRequest.getHeaderMap(), httpRequest.getCookieMap())
        } catch (e: Exception) {
            throw IOException(e.message)
        }
    }

    /**
     * 内部发送HTTP请求的主要方法
     *
     * @param uri        URI
     * @param uriVars    url中的请求参数
     * @param httpMethod HTTP请求方法
     * @param body       请求的body实体
     * @param headers    HTTP请求的header
     * @return 基于Reactive-Mono的异步返回实体，结果为 byte[] 类型.
     */
    private fun send(
            uri: String, uriVars: Map<String, String?>?, httpMethod: HttpMethod, mediaType: MediaType,
            body: Any?, headers: Map<String, String>?, cookies: Map<String, String>?
    ): Mono<ByteArray> {
        val method = webClient.method(httpMethod)
        method.contentType(mediaType)
        if (body != null) {
            method.bodyValue(body)
        }
        if (headers != null && headers.isNotEmpty()) {
            method.headers { httpHeaders: HttpHeaders -> httpHeaders.addAll(HttpHeaders(httpHeaders)) }
        }
        if (uriVars == null || uriVars.isEmpty()) {
            method.uri(uri)
        } else {
            method.uri(uri, uriVars)
        }
        if (cookies != null && cookies.isNotEmpty()) {
            cookies.forEach(BiConsumer { name: String?, value: String? -> method.cookie(name!!, value!!) })
        }
        return method
                .retrieve()
                .onStatus({ obj: HttpStatus -> obj.is3xxRedirection }, { response: ClientResponse ->
                    Mono.error(
                            IOException("HTTP请求返回跳转行为:" + response.statusCode()))
                })
                .onStatus({ obj: HttpStatus -> obj.isError }) { obj: ClientResponse -> obj.createException() }
                .bodyToMono(ByteArray::class.java)
    }

    /**
     * 反序列化
     */
    private fun <T> jsonSerialize(strBytes: ByteArray?, clazz: Class<T>): T {
        return if (clazz == String::class.java) {
            String(strBytes!!) as T
        } else {
            objectMapper.readValue(strBytes, clazz)
        }
    }

    /**
     * 反序列化
     */
    private fun <T> jsonSerialize(strBytes: ByteArray?, typeReference: TypeReference<T>): T {
        return if (typeReference.type.typeName == String::class.java.typeName) {
            String(strBytes!!) as T
        } else {
            objectMapper.readValue(strBytes, typeReference)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(HttpService::class.java)
    }

    init {
        val httpClient = HttpClient.create()
                .tcpConfiguration {
                    it.doOnConnected { conn: Connection ->
                        conn.addHandlerLast(ReadTimeoutHandler(10))
                                .addHandlerLast(WriteTimeoutHandler(10))
                    }
                }
        webClient = builder.clientConnector(ReactorClientHttpConnector(httpClient)).build()
    }
}