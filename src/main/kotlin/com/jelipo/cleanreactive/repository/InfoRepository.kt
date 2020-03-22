package com.jelipo.cleanreactive.repository

import com.jelipo.cleanreactive.entity.Info
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

/**
 *
 * @author Jelipo
 * @date 2020/3/22 23:38
 */
@Repository
interface InfoRepository : R2dbcRepository<Info, Long> {


}