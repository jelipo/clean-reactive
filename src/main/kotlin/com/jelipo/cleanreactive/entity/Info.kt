package com.jelipo.cleanreactive.entity

import org.springframework.data.annotation.Id


/**
 *
 * @author Jelipo
 * @date 2020/3/22 23:43
 */

open class Info(

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

open class AiData(
        id: Long=1,
        name: String = ""
) : Info(id, name)