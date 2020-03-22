package com.jelipo.cleanreactive.service

import com.jelipo.cleanreactive.entity.Info
import com.jelipo.cleanreactive.repository.InfoRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

/**
 *
 * @author Jelipo
 * @date 2020/3/22 23:37
 */
@Service
class TestService(
        private val infoRepository: InfoRepository
) {

    suspend fun getAllInfo(): Flux<Info> {
        return infoRepository.findAll()
    }

}