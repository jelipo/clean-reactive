package com.jelipo.cleanreactive.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 *
 * @author Jelipo
 * @date 2020/3/22 23:43
 */
@Table("info")
data class Info(

        /**
         * 主键ID
         */
        @Id
        val id: Long,

        /**
         * 名称
         */
        val name: String = ""
)