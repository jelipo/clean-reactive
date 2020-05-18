package com.jelipo.cleanreactive.ctrl

import com.jelipo.cleanreactive.entity.AiData
import com.jelipo.cleanreactive.entity.Info
import com.jelipo.cleanreactive.service.TestService
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
class TestCtrl(
        private val testService: TestService
) {

    @GetMapping("test")
    suspend fun test(): String {
        println(AiData().name)
        return testService.getAllInfo("1")
    }

}