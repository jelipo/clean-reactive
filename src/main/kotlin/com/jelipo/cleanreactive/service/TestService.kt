package com.jelipo.cleanreactive.service

import com.jelipo.cleanreactive.http.HttpBuilder
import com.jelipo.cleanreactive.http.HttpService
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

/**
 *
 * @author Jelipo
 * @date 2020/3/22 23:37
 */
@Service
class TestService(
        private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
        private val httpService: HttpService
) {

    suspend fun getAllInfo(key: String): String {
        var value: String? = reactiveRedisTemplate.opsForValue().get(key).awaitFirstOrNull()
        if (value == null) {
            value = getFromUrl(key)
            setCache(key, value)
        }
        return value
    }

    suspend fun getFromUrl(key: String): String {
        return httpService.monoRequest(HttpBuilder.get("http://www.baidu.com"), String::class.java).awaitFirst()
    }

    suspend fun setCache(key: String, info: String) {
        reactiveRedisTemplate.opsForValue().set(key, info, Duration.ofSeconds(10)).awaitFirst()
    }

}