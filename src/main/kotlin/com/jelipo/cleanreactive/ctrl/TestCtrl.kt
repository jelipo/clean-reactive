package com.jelipo.cleanreactive.ctrl

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 *
 * @author Jelipo
 * @date 2020/3/22 22:56
 */
@RestController
@RequestMapping("/")
class TestCtrl {

    @GetMapping
    suspend fun test(): String {
        return ""
    }

}