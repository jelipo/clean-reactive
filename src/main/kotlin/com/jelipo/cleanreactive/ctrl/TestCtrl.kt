package com.jelipo.cleanreactive.ctrl

import com.jelipo.cleanreactive.entity.Info
import com.jelipo.cleanreactive.service.TestService
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    private lateinit var testService: TestService

    @GetMapping
    suspend fun test(): MutableIterable<Info> {
        return testService.getAllInfo()
    }

}