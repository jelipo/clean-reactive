package com.jelipo.cleanreactive.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.jelipo.cleanreactive.http.HttpService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 *
 * @author Jelipo
 * @date 2020/3/22 22:53
 */
@Configuration
class SpringConfig {

    /**
     * 生成全局 Jackson 的 [ObjectMapper] , 支持 Kotlin
     */
    @Bean
    fun getObjectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            findAndRegisterModules()
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }

    @Bean
    fun getHttpService(objectMapper: ObjectMapper, builder: WebClient.Builder): HttpService {
        return HttpService(objectMapper, builder)
    }

}