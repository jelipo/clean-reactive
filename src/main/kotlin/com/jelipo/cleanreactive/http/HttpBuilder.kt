package com.jelipo.cleanreactive.http

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import java.io.IOException

/**
 * 构建Http请求的主要类
 *
 * @author Jelipo
 * @date 2020/05/18 21:04
 */
class HttpBuilder private constructor(private val baseUri: String) {
    fun getWithPath(path: String): HttpRequest {
        return get(buildUrl(baseUri, path))
    }

    fun postWithPath(path: String, body: Any?): HttpRequest {
        return post(buildUrl(baseUri, path), body)
    }

    fun deleteWithPath(path: String): HttpRequest {
        return delete(buildUrl(baseUri, path))
    }

    fun putWithPath(path: String, body: Any?): HttpRequest {
        return put(buildUrl(baseUri, path), body)
    }

    fun fromWithPath(httpMethod: String, path: String, data: Any?): HttpRequest {
        return from(httpMethod, buildUrl(baseUri, path), data)
    }

    class HttpRequest(val url: String, val httpMethod: HttpMethod, val body: Any?) {

        /**
         * 默认使用 ‘application/json’ 作为 Content-Type 的 MediaType
         */
        private var mediaType = MediaType.APPLICATION_JSON
        private var headerMap: MutableMap<String, String>? = null
        private var cookieMap: MutableMap<String, String>? = null


        /**
         * 使 HTTP 的 Content-Type 为 ‘application/json’
         * body支持普通 Pojo类 、[Map] 和 [String] 类型
         */
        fun jsonMediaContent(): HttpRequest {
            mediaType = MediaType.APPLICATION_JSON
            return this
        }

        /**
         * 使 HTTP 的Content-Type 为 ‘application/x-www-form-urlencoded’
         * body支持 [Map] 类型
         */
        fun formUrlMediaContent(): HttpRequest {
            mediaType = MediaType.APPLICATION_FORM_URLENCODED
            return this
        }

        /**
         * 使用自定义的 Content-Type
         */
        fun customContent(mediaType: MediaType): HttpRequest {
            this.mediaType = mediaType
            return this
        }

        /**
         * 设置HTTP请求的Headers，此方法会覆盖之前设置的Header
         */
        fun headers(headerMap: MutableMap<String, String>?): HttpRequest {
            this.headerMap = headerMap
            return this
        }

        /**
         * 添加Http的请求Header
         */
        fun addHeader(key: String, value: String): HttpRequest {
            if (headerMap == null) {
                headerMap = HashMap(2)
            }
            headerMap!![key] = value
            return this
        }

        /**
         * 设置HTTP请求的Headers，此方法会覆盖之前设置的Header
         */
        fun cookies(cookieMap: MutableMap<String, String>?): HttpRequest {
            this.cookieMap = cookieMap
            return this
        }

        /**
         * 添加Http的请求Header
         */
        fun addCookie(key: String, value: String): HttpRequest {
            if (cookieMap == null) {
                cookieMap = HashMap(2)
            }
            cookieMap!![key] = value
            return this
        }

        fun getHeaderMap(): MutableMap<String, String>? {
            return this.headerMap
        }

        fun getCookieMap(): MutableMap<String, String>? {
            return this.cookieMap
        }

        fun getMediaType(): MediaType {
            return this.mediaType
        }

    }

    companion object {
        private const val PATH_SPLIT = "/"

        /**
         * 构建一个不可变的HttpBuilder
         */
        fun creat(baseUri: String): HttpBuilder {
            return HttpBuilder(baseUri)
        }

        fun buildUrl(baseUri: String, path: String): String {
            return if (baseUri.endsWith(PATH_SPLIT) && path.startsWith(PATH_SPLIT)) {
                baseUri + path.substring(1)
            } else if (!baseUri.endsWith(PATH_SPLIT) && !path.startsWith(PATH_SPLIT)) {
                baseUri + PATH_SPLIT + path
            } else {
                baseUri + path
            }
        }

        //----------------------------------static------------------------------------//
        operator fun get(url: String): HttpRequest {
            return HttpRequest(url, HttpMethod.GET, null)
        }

        /**
         * body支持 [String]、[Map]类型(JSON和表单)、实体Pojo类型(JSON)
         */
        fun post(url: String, body: Any?): HttpRequest {
            return HttpRequest(url, HttpMethod.POST, body)
        }

        fun delete(url: String): HttpRequest {
            return HttpRequest(url, HttpMethod.DELETE, null)
        }

        /**
         * body支持 [String]、[Map]类型(JSON和表单)、实体Pojo类型(JSON)
         */
        fun put(url: String, body: Any?): HttpRequest {
            return HttpRequest(url, HttpMethod.PUT, body)
        }

        /**
         * 此方法适用于字符串匹配HTTP的方法。
         *
         * @param httpMethod 目前只支持 get/post/delete/put
         */
        @Throws(IOException::class)
        fun from(httpMethod: String, url: String, data: Any?): HttpRequest {
            return when (httpMethod.toLowerCase()) {
                "get" -> get(url)
                "post" -> post(url, data)
                "delete" -> delete(url)
                "put" -> put(url, data)
                else -> throw IOException("未知的HTTP请求方法$httpMethod")
            }
        }
    }

}