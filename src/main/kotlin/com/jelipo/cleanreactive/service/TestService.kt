package com.jelipo.cleanreactive.service

import com.jelipo.cleanreactive.entity.Info
import com.jelipo.cleanreactive.repository.InfoRepository
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.stereotype.Service

/**
 *
 * @author Jelipo
 * @date 2020/3/22 23:37
 */
@Service
class TestService(
        private val infoRepository: InfoRepository
) {

    suspend fun getAllInfo(): List<Info> {
        return infoRepository.findAll().collectList().awaitFirst()
    }

}