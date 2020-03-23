package com.jelipo.cleanreactive.repository

import com.jelipo.cleanreactive.entity.Info
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

/**
 *
 * @author Jelipo
 * @date 2020/3/22 23:38
 */
@Repository
interface InfoRepository : R2dbcRepository<Info, Long> {

    @Query("select * from info where name = :name")
    fun findsome(name: String): Flux<Info>

}